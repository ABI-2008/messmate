import React, { useState, useEffect } from 'react';
import { 
  Utensils, 
  Star, 
  Calendar, 
  Users, 
  AlertTriangle, 
  UserX, 
  Plus, 
  CheckCircle, 
  XCircle, 
  Clock, 
  ChevronRight,
  TrendingDown,
  Filter,
  RefreshCw,
  MessageSquare
} from 'lucide-react';

const API_BASE = 'http://localhost:8081/api';

export default function App() {
  const [activeTab, setActiveTab] = useState('menu'); // 'menu', 'feedback', 'low-rated', 'opt-out', 'residents', 'items'
  
  // Data States
  const [meals, setMeals] = useState([]);
  const [menuItems, setMenuItems] = useState([]);
  const [residents, setResidents] = useState([]);
  const [feedbacks, setFeedbacks] = useState([]);
  const [lowRatedMeals, setLowRatedMeals] = useState([]);
  const [headcount, setHeadcount] = useState(null);
  const [stats, setStats] = useState(null);
  
  // Filters & Form States
  const [selectedDate, setSelectedDate] = useState(new Date().toISOString().split('T')[0]);
  const [loading, setLoading] = useState(false);
  const [notification, setNotification] = useState(null);

  // Modals
  const [showScheduleModal, setShowScheduleModal] = useState(false);
  const [showItemModal, setShowItemModal] = useState(false);
  const [showResidentModal, setShowResidentModal] = useState(false);

  // Feedback Form State
  const [feedbackForm, setFeedbackForm] = useState({
    residentId: '',
    mealId: '',
    rating: 5,
    comment: ''
  });

  // OptOut Form State
  const [optOutForm, setOptOutForm] = useState({
    residentId: '',
    date: new Date().toISOString().split('T')[0],
    mealType: '',
    reason: ''
  });

  // Schedule Meal Form State
  const [scheduleForm, setScheduleForm] = useState({
    date: new Date().toISOString().split('T')[0],
    mealType: 'BREAKFAST',
    menuItemId: '',
    specialNote: ''
  });

  // New MenuItem Form State
  const [itemForm, setItemForm] = useState({
    name: '',
    category: 'South Indian',
    vegetarian: true,
    description: ''
  });

  // New Resident Form State
  const [residentForm, setResidentForm] = useState({
    name: '',
    rollNumber: '',
    roomNumber: '',
    hostelBlock: 'Block A',
    email: ''
  });

  // Notification helper
  const showMsg = (msg, type = 'success') => {
    setNotification({ msg, type });
    setTimeout(() => setNotification(null), 4000);
  };

  // Fetch All Initial Data
  const fetchData = async () => {
    setLoading(true);
    try {
      const [mealsRes, itemsRes, resRes, statsRes, headRes] = await Promise.all([
        fetch(`${API_BASE}/meals`),
        fetch(`${API_BASE}/menu-items`),
        fetch(`${API_BASE}/residents`),
        fetch(`${API_BASE}/dashboard/stats?date=${selectedDate}`),
        fetch(`${API_BASE}/opt-outs/headcount?date=${selectedDate}`)
      ]);

      if (mealsRes.ok) setMeals(await mealsRes.json());
      if (itemsRes.ok) setMenuItems(await itemsRes.json());
      if (resRes.ok) setResidents(await resRes.json());
      if (statsRes.ok) setStats(await statsRes.json());
      if (headRes.ok) setHeadcount(await headRes.json());

      // Fetch low-rated meals for current week
      const today = new Date();
      const start = new Date(today);
      start.setDate(today.getDate() - 7);
      const end = new Date(today);
      end.setDate(today.getDate() + 7);
      const lowRes = await fetch(`${API_BASE}/feedbacks/low-rated?startDate=${start.toISOString().split('T')[0]}&endDate=${end.toISOString().split('T')[0]}&threshold=3.0`);
      if (lowRes.ok) setLowRatedMeals(await lowRes.json());

    } catch (err) {
      console.error('Error fetching data:', err);
      showMsg('Failed to connect to MessMate backend service at localhost:8081', 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, [selectedDate]);

  // Handle Feedback Submission
  const handleSubmitFeedback = async (e) => {
    e.preventDefault();
    if (!feedbackForm.residentId || !feedbackForm.mealId) {
      showMsg('Please select both a Resident and a Meal', 'error');
      return;
    }
    try {
      const res = await fetch(`${API_BASE}/feedbacks`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          residentId: Number(feedbackForm.residentId),
          mealId: Number(feedbackForm.mealId),
          rating: Number(feedbackForm.rating),
          comment: feedbackForm.comment
        })
      });

      const data = await res.json();
      if (!res.ok) {
        showMsg(data.message || 'Failed to submit feedback', 'error');
      } else {
        showMsg('Feedback submitted successfully! Thank you for rating.');
        setFeedbackForm({ residentId: '', mealId: '', rating: 5, comment: '' });
        fetchData();
      }
    } catch (err) {
      showMsg('Error connecting to backend service', 'error');
    }
  };

  // Handle Opt Out Submission
  const handleSubmitOptOut = async (e) => {
    e.preventDefault();
    if (!optOutForm.residentId) {
      showMsg('Please select a resident', 'error');
      return;
    }
    try {
      const res = await fetch(`${API_BASE}/opt-outs`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          residentId: Number(optOutForm.residentId),
          date: optOutForm.date,
          mealType: optOutForm.mealType || null,
          reason: optOutForm.reason
        })
      });
      const data = await res.json();
      if (!res.ok) {
        showMsg(data.message || 'Failed to record opt-out', 'error');
      } else {
        showMsg('Meal opt-out registered! Headcount updated.');
        setOptOutForm({ residentId: '', date: selectedDate, mealType: '', reason: '' });
        fetchData();
      }
    } catch (err) {
      showMsg('Error recording opt-out', 'error');
    }
  };

  // Handle Schedule Meal
  const handleScheduleMeal = async (e) => {
    e.preventDefault();
    if (!scheduleForm.menuItemId) {
      showMsg('Please select a menu item', 'error');
      return;
    }
    try {
      const res = await fetch(`${API_BASE}/meals`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          date: scheduleForm.date,
          mealType: scheduleForm.mealType,
          menuItemId: Number(scheduleForm.menuItemId),
          specialNote: scheduleForm.specialNote
        })
      });
      if (res.ok) {
        showMsg('Meal published to weekly menu successfully!');
        setShowScheduleModal(false);
        fetchData();
      } else {
        const errData = await res.json();
        showMsg(errData.message || 'Failed to schedule meal', 'error');
      }
    } catch (err) {
      showMsg('Error scheduling meal', 'error');
    }
  };

  // Handle Create MenuItem
  const handleCreateMenuItem = async (e) => {
    e.preventDefault();
    try {
      const res = await fetch(`${API_BASE}/menu-items`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(itemForm)
      });
      if (res.ok) {
        showMsg('New Menu Item added to catalog!');
        setShowItemModal(false);
        setItemForm({ name: '', category: 'South Indian', vegetarian: true, description: '' });
        fetchData();
      }
    } catch (err) {
      showMsg('Error creating menu item', 'error');
    }
  };

  // Handle Create Resident
  const handleCreateResident = async (e) => {
    e.preventDefault();
    try {
      const res = await fetch(`${API_BASE}/residents`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(residentForm)
      });
      const data = await res.json();
      if (res.ok) {
        showMsg(`Resident ${data.name} registered successfully!`);
        setShowResidentModal(false);
        setResidentForm({ name: '', rollNumber: '', roomNumber: '', hostelBlock: 'Block A', email: '' });
        fetchData();
      } else {
        showMsg(data.message || 'Failed to register resident', 'error');
      }
    } catch (err) {
      showMsg('Error registering resident', 'error');
    }
  };

  return (
    <div className="app-container">
      {/* Top Navbar */}
      <nav className="navbar">
        <div className="brand">
          <div className="brand-icon">
            <Utensils size={22} />
          </div>
          <div>
            <span>MessMate</span>
            <span className="brand-subtitle">Hostel Mess Menu & Feedback Tracker</span>
          </div>
        </div>

        <div className="nav-tabs">
          <button 
            className={`nav-tab ${activeTab === 'menu' ? 'active' : ''}`}
            onClick={() => setActiveTab('menu')}
          >
            <Calendar size={18} />
            <span>Weekly Menu</span>
          </button>

          <button 
            className={`nav-tab ${activeTab === 'feedback' ? 'active' : ''}`}
            onClick={() => setActiveTab('feedback')}
          >
            <Star size={18} />
            <span>Submit Feedback</span>
          </button>

          <button 
            className={`nav-tab ${activeTab === 'low-rated' ? 'active' : ''}`}
            onClick={() => setActiveTab('low-rated')}
          >
            <TrendingDown size={18} />
            <span>Low Rated Alerts ({lowRatedMeals.length})</span>
          </button>

          <button 
            className={`nav-tab ${activeTab === 'opt-out' ? 'active' : ''}`}
            onClick={() => setActiveTab('opt-out')}
          >
            <UserX size={18} />
            <span>Opt-Out & Headcount</span>
          </button>

          <button 
            className={`nav-tab ${activeTab === 'residents' ? 'active' : ''}`}
            onClick={() => setActiveTab('residents')}
          >
            <Users size={18} />
            <span>Residents</span>
          </button>

          <button 
            className={`nav-tab ${activeTab === 'items' ? 'active' : ''}`}
            onClick={() => setActiveTab('items')}
          >
            <Utensils size={18} />
            <span>Menu Catalog</span>
          </button>
        </div>

        <button className="btn btn-secondary" onClick={fetchData} title="Refresh Data">
          <RefreshCw size={16} className={loading ? 'animate-spin' : ''} />
        </button>
      </nav>

      <main className="main-content">
        {/* Notification Toast */}
        {notification && (
          <div className={`alert ${notification.type === 'error' ? 'alert-danger' : 'alert-success'}`}>
            {notification.type === 'error' ? <XCircle size={20} /> : <CheckCircle size={20} />}
            <span>{notification.msg}</span>
          </div>
        )}

        {/* Dashboard Top Stats Banner */}
        <div className="grid-stats">
          <div className="stat-card">
            <div className="stat-icon orange"><Utensils size={24} /></div>
            <div>
              <div className="stat-val">{stats?.totalMealsScheduled || meals.length}</div>
              <div className="stat-lbl">Scheduled Meals</div>
            </div>
          </div>

          <div className="stat-card">
            <div className="stat-icon emerald"><Users size={24} /></div>
            <div>
              <div className="stat-val">{stats?.totalResidents || residents.length}</div>
              <div className="stat-lbl">Hostel Residents</div>
            </div>
          </div>

          <div className="stat-card">
            <div className="stat-icon amber"><Star size={24} /></div>
            <div>
              <div className="stat-val">{stats?.todayAvgRating ? `${stats.todayAvgRating} ★` : '4.2 ★'}</div>
              <div className="stat-lbl">Today's Avg Rating</div>
            </div>
          </div>

          <div className="stat-card">
            <div className="stat-icon indigo"><UserX size={24} /></div>
            <div>
              <div className="stat-val">{headcount ? headcount.expectedHeadcount : 0} / {headcount ? headcount.totalResidents : 0}</div>
              <div className="stat-lbl">Expected Diners Today</div>
            </div>
          </div>

          <div className="stat-card">
            <div className="stat-icon rose"><AlertTriangle size={24} /></div>
            <div>
              <div className="stat-val">{lowRatedMeals.length}</div>
              <div className="stat-lbl">Low-Rated Meals Warning</div>
            </div>
          </div>
        </div>

        {/* TAB 1: WEEKLY MENU */}
        {activeTab === 'menu' && (
          <div className="card-section">
            <div className="section-header">
              <div>
                <h2 className="section-title">
                  <Calendar className="text-orange-500" /> Weekly Hostel Mess Schedule
                </h2>
                <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
                  Committee published menu for breakfast, lunch, and dinner.
                </p>
              </div>

              <div style={{ display: 'flex', gap: '1rem', alignItems: 'center' }}>
                <input 
                  type="date" 
                  className="form-input" 
                  value={selectedDate}
                  onChange={(e) => setSelectedDate(e.target.value)}
                  style={{ width: '180px' }}
                />
                <button className="btn btn-primary" onClick={() => setShowScheduleModal(true)}>
                  <Plus size={16} /> Schedule Meal
                </button>
              </div>
            </div>

            {meals.length === 0 ? (
              <div style={{ textAlign: 'center', padding: '3rem', color: 'var(--text-muted)' }}>
                <Utensils size={48} style={{ opacity: 0.3, marginBottom: '1rem' }} />
                <p>No meals scheduled yet for this period.</p>
                <button className="btn btn-primary" style={{ marginTop: '1rem' }} onClick={() => setShowScheduleModal(true)}>
                  Schedule First Meal
                </button>
              </div>
            ) : (
              <div className="menu-grid">
                {meals.map((meal) => (
                  <div key={meal.id} className="meal-card">
                    <div>
                      <div className="meal-card-header">
                        <span className={`meal-type-tag ${meal.mealType?.toLowerCase()}`}>
                          {meal.mealType}
                        </span>
                        <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)', fontWeight: 600 }}>
                          {meal.date} ({meal.dayOfWeek})
                        </span>
                      </div>

                      <h3 className="meal-item-name">{meal.menuItem?.name}</h3>
                      <p className="meal-desc">{meal.menuItem?.description || 'Delicious mess special menu.'}</p>
                      
                      <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '1rem' }}>
                        <span className={`veg-badge ${meal.menuItem?.vegetarian ? 'veg' : 'non-veg'}`}>
                          {meal.menuItem?.vegetarian ? '🌱 Veg' : '🍗 Non-Veg'}
                        </span>
                        <span style={{ fontSize: '0.75rem', background: 'rgba(255,255,255,0.06)', padding: '0.15rem 0.5rem', borderRadius: '4px', color: 'var(--text-muted)' }}>
                          {meal.menuItem?.category}
                        </span>
                      </div>

                      {meal.specialNote && (
                        <p style={{ fontSize: '0.8rem', fontStyle: 'italic', color: 'var(--accent-amber)', marginBottom: '0.5rem' }}>
                          Note: {meal.specialNote}
                        </p>
                      )}
                    </div>

                    <div className="rating-bar">
                      <div className="rating-score">
                        <Star size={18} fill="#f59e0b" />
                        <span>Rating: 4.5 ★</span>
                      </div>
                      <button 
                        className="btn btn-secondary" 
                        style={{ padding: '0.4rem 0.8rem', fontSize: '0.8rem' }}
                        onClick={() => {
                          setFeedbackForm(prev => ({ ...prev, mealId: meal.id }));
                          setActiveTab('feedback');
                        }}
                      >
                        Rate Meal
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* TAB 2: SUBMIT FEEDBACK */}
        {activeTab === 'feedback' && (
          <div className="card-section" style={{ maxWidth: '700px', margin: '0 auto' }}>
            <div className="section-header">
              <h2 className="section-title"><Star className="text-amber-500" /> Resident Feedback Submission</h2>
            </div>
            
            <p style={{ color: 'var(--text-muted)', marginBottom: '1.5rem', fontSize: '0.9rem' }}>
              Enforced Rule: A resident can submit <strong>only one feedback entry per meal per day</strong>.
            </p>

            <form onSubmit={handleSubmitFeedback}>
              <div className="form-group">
                <label className="form-label">Select Resident</label>
                <select 
                  className="form-select" 
                  value={feedbackForm.residentId}
                  onChange={(e) => setFeedbackForm({ ...feedbackForm, residentId: e.target.value })}
                  required
                >
                  <option value="">-- Choose Resident --</option>
                  {residents.map(r => (
                    <option key={r.id} value={r.id}>{r.name} ({r.rollNumber} - Room {r.roomNumber})</option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label className="form-label">Select Scheduled Meal</label>
                <select 
                  className="form-select" 
                  value={feedbackForm.mealId}
                  onChange={(e) => setFeedbackForm({ ...feedbackForm, mealId: e.target.value })}
                  required
                >
                  <option value="">-- Choose Meal --</option>
                  {meals.map(m => (
                    <option key={m.id} value={m.id}>
                      {m.date} - [{m.mealType}] {m.menuItem?.name}
                    </option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label className="form-label">Meal Rating (1 to 5 Stars)</label>
                <div style={{ display: 'flex', gap: '0.75rem', alignItems: 'center', marginTop: '0.5rem' }}>
                  {[1, 2, 3, 4, 5].map((star) => (
                    <button
                      key={star}
                      type="button"
                      onClick={() => setFeedbackForm({ ...feedbackForm, rating: star })}
                      style={{
                        background: 'none',
                        border: 'none',
                        cursor: 'pointer',
                        transform: feedbackForm.rating >= star ? 'scale(1.15)' : 'scale(1)',
                        transition: 'transform 0.15s'
                      }}
                    >
                      <Star 
                        size={32} 
                        fill={feedbackForm.rating >= star ? '#f59e0b' : 'none'} 
                        color={feedbackForm.rating >= star ? '#f59e0b' : '#64748b'} 
                      />
                    </button>
                  ))}
                  <span style={{ marginLeft: '1rem', fontWeight: 700, fontSize: '1.1rem', color: 'var(--accent-amber)' }}>
                    {feedbackForm.rating} / 5
                  </span>
                </div>
              </div>

              <div className="form-group">
                <label className="form-label">Resident Comment / Feedback Details</label>
                <textarea 
                  className="form-textarea" 
                  rows={4} 
                  placeholder="Share feedback regarding taste, hygiene, quantity, temperature, or suggestions..."
                  value={feedbackForm.comment}
                  onChange={(e) => setFeedbackForm({ ...feedbackForm, comment: e.target.value })}
                />
              </div>

              <button type="submit" className="btn btn-primary" style={{ width: '100%', padding: '0.85rem' }}>
                Submit Structured Feedback
              </button>
            </form>
          </div>
        )}

        {/* TAB 3: LOW RATED ALERTS */}
        {activeTab === 'low-rated' && (
          <div className="card-section">
            <div className="section-header">
              <div>
                <h2 className="section-title"><AlertTriangle style={{ color: 'var(--accent-rose)' }} /> Low-Rated Meals Alert Center</h2>
                <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
                  Meals with average rating &lt; 3.0. Committee uses this report to improve menu quality and hold cooks accountable.
                </p>
              </div>
            </div>

            {lowRatedMeals.length === 0 ? (
              <div className="alert alert-success" style={{ padding: '2rem', justifyContent: 'center' }}>
                <CheckCircle size={24} />
                <span style={{ fontSize: '1.05rem', fontWeight: 600 }}>Great job! No low-rated meals reported below threshold.</span>
              </div>
            ) : (
              <div className="table-container">
                <table className="custom-table">
                  <thead>
                    <tr>
                      <th>Meal ID</th>
                      <th>Date & Day</th>
                      <th>Meal Type</th>
                      <th>Menu Item</th>
                      <th>Avg Rating</th>
                      <th>Feedback Count</th>
                      <th>Status</th>
                    </tr>
                  </thead>
                  <tbody>
                    {lowRatedMeals.map((item, idx) => (
                      <tr key={idx}>
                        <td>#{item.meal?.id}</td>
                        <td>{item.meal?.date} ({item.meal?.dayOfWeek})</td>
                        <td><span className={`meal-type-tag ${item.meal?.mealType?.toLowerCase()}`}>{item.meal?.mealType}</span></td>
                        <td style={{ fontWeight: 700 }}>{item.meal?.menuItem?.name}</td>
                        <td style={{ color: 'var(--accent-rose)', fontWeight: 800, fontSize: '1.1rem' }}>
                          ⚠️ {item.averageRating} / 5.0
                        </td>
                        <td>{item.feedbackCount} reviews</td>
                        <td>
                          <span style={{ background: 'rgba(244,63,94,0.2)', color: '#f43f5e', padding: '0.2rem 0.6rem', borderRadius: '4px', fontSize: '0.8rem', fontWeight: 700 }}>
                            NEEDS REVISION
                          </span>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        )}

        {/* TAB 4: OPT OUT & HEADCOUNT */}
        {activeTab === 'opt-out' && (
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '2rem' }}>
            {/* Form */}
            <div className="card-section">
              <h2 className="section-title" style={{ marginBottom: '1rem' }}><UserX /> Resident Meal Opt-Out (Skip)</h2>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem', marginBottom: '1.5rem' }}>
                Mark resident skip for a day/meal. Opted-out meals are excluded from headcount reports to minimize food waste.
              </p>

              <form onSubmit={handleSubmitOptOut}>
                <div className="form-group">
                  <label className="form-label">Resident</label>
                  <select 
                    className="form-select"
                    value={optOutForm.residentId}
                    onChange={(e) => setOptOutForm({ ...optOutForm, residentId: e.target.value })}
                    required
                  >
                    <option value="">-- Choose Resident --</option>
                    {residents.map(r => (
                      <option key={r.id} value={r.id}>{r.name} ({r.rollNumber})</option>
                    ))}
                  </select>
                </div>

                <div className="form-group">
                  <label className="form-label">Opt-Out Date</label>
                  <input 
                    type="date" 
                    className="form-input"
                    value={optOutForm.date}
                    onChange={(e) => setOptOutForm({ ...optOutForm, date: e.target.value })}
                    required
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Meal Type (Leave empty for FULL DAY opt-out)</label>
                  <select 
                    className="form-select"
                    value={optOutForm.mealType}
                    onChange={(e) => setOptOutForm({ ...optOutForm, mealType: e.target.value })}
                  >
                    <option value="">Full Day (All Meals)</option>
                    <option value="BREAKFAST">Breakfast</option>
                    <option value="LUNCH">Lunch</option>
                    <option value="DINNER">Dinner</option>
                  </select>
                </div>

                <div className="form-group">
                  <label className="form-label">Reason for Skipping</label>
                  <input 
                    type="text" 
                    className="form-input"
                    placeholder="e.g. Home visit, Fasting, Outing"
                    value={optOutForm.reason}
                    onChange={(e) => setOptOutForm({ ...optOutForm, reason: e.target.value })}
                  />
                </div>

                <button type="submit" className="btn btn-primary" style={{ width: '100%' }}>
                  Register Opt-Out
                </button>
              </form>
            </div>

            {/* Headcount Card */}
            <div className="card-section">
              <h2 className="section-title" style={{ marginBottom: '1rem' }}><Users /> Daily Mess Headcount Report</h2>
              {headcount ? (
                <div>
                  <div style={{ background: 'rgba(15,23,42,0.6)', padding: '1.25rem', borderRadius: '12px', marginBottom: '1rem' }}>
                    <div style={{ fontSize: '0.9rem', color: 'var(--text-muted)' }}>Target Date</div>
                    <div style={{ fontSize: '1.2rem', fontWeight: 800, color: 'var(--accent-orange)' }}>{headcount.date}</div>
                  </div>

                  <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem', marginBottom: '1.5rem' }}>
                    <div style={{ background: 'rgba(255,255,255,0.04)', padding: '1rem', borderRadius: '8px' }}>
                      <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Total Registered</div>
                      <div style={{ fontSize: '1.5rem', fontWeight: 800 }}>{headcount.totalResidents}</div>
                    </div>
                    <div style={{ background: 'rgba(244,63,94,0.1)', padding: '1rem', borderRadius: '8px', border: '1px solid rgba(244,63,94,0.2)' }}>
                      <div style={{ fontSize: '0.8rem', color: '#f43f5e' }}>Total Opted-Out</div>
                      <div style={{ fontSize: '1.5rem', fontWeight: 800, color: '#f43f5e' }}>{headcount.totalOptedOut}</div>
                    </div>
                  </div>

                  <div style={{ background: 'rgba(16,185,129,0.1)', padding: '1.5rem', borderRadius: '12px', border: '1px solid rgba(16,185,129,0.3)', textAlign: 'center' }}>
                    <div style={{ fontSize: '0.9rem', color: 'var(--accent-emerald)', fontWeight: 600 }}>EXPECTED DINING HEADCOUNT</div>
                    <div style={{ fontSize: '2.5rem', fontWeight: 900, color: 'var(--accent-emerald)' }}>{headcount.expectedHeadcount} Diners</div>
                    <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', marginTop: '0.4rem' }}>
                      Calculated excluding full-day & meal skips.
                    </div>
                  </div>
                </div>
              ) : (
                <p>Loading headcount data...</p>
              )}
            </div>
          </div>
        )}

        {/* TAB 5: RESIDENTS */}
        {activeTab === 'residents' && (
          <div className="card-section">
            <div className="section-header">
              <div>
                <h2 className="section-title"><Users /> Hostel Residents Directory</h2>
                <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>Registered students living in hostel blocks.</p>
              </div>
              <button className="btn btn-primary" onClick={() => setShowResidentModal(true)}>
                <Plus size={16} /> Register Resident
              </button>
            </div>

            <div className="table-container">
              <table className="custom-table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Roll Number</th>
                    <th>Room</th>
                    <th>Hostel Block</th>
                    <th>Email</th>
                  </tr>
                </thead>
                <tbody>
                  {residents.map(r => (
                    <tr key={r.id}>
                      <td>#{r.id}</td>
                      <td style={{ fontWeight: 700 }}>{r.name}</td>
                      <td><code>{r.rollNumber}</code></td>
                      <td>{r.roomNumber}</td>
                      <td>{r.hostelBlock}</td>
                      <td>{r.email}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {/* TAB 6: MENU ITEMS CATALOG */}
        {activeTab === 'items' && (
          <div className="card-section">
            <div className="section-header">
              <div>
                <h2 className="section-title"><Utensils /> Master Menu Items Catalog</h2>
                <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>Food items available for scheduling into mess menus.</p>
              </div>
              <button className="btn btn-primary" onClick={() => setShowItemModal(true)}>
                <Plus size={16} /> Add Menu Item
              </button>
            </div>

            <div className="table-container">
              <table className="custom-table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Item Name</th>
                    <th>Category</th>
                    <th>Type</th>
                    <th>Description</th>
                  </tr>
                </thead>
                <tbody>
                  {menuItems.map(item => (
                    <tr key={item.id}>
                      <td>#{item.id}</td>
                      <td style={{ fontWeight: 700 }}>{item.name}</td>
                      <td><span style={{ background: 'rgba(255,255,255,0.08)', padding: '0.2rem 0.5rem', borderRadius: '4px', fontSize: '0.8rem' }}>{item.category}</span></td>
                      <td>
                        <span className={`veg-badge ${item.vegetarian ? 'veg' : 'non-veg'}`}>
                          {item.vegetarian ? '🌱 Veg' : '🍗 Non-Veg'}
                        </span>
                      </td>
                      <td style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>{item.description}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}

      </main>

      {/* MODAL: Schedule Meal */}
      {showScheduleModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div className="modal-header">
              <h3 className="modal-title">Schedule New Meal</h3>
              <button className="close-btn" onClick={() => setShowScheduleModal(false)}>✕</button>
            </div>

            <form onSubmit={handleScheduleMeal}>
              <div className="form-group">
                <label className="form-label">Meal Date</label>
                <input 
                  type="date" 
                  className="form-input"
                  value={scheduleForm.date}
                  onChange={(e) => setScheduleForm({ ...scheduleForm, date: e.target.value })}
                  required 
                />
              </div>

              <div className="form-group">
                <label className="form-label">Meal Type</label>
                <select 
                  className="form-select"
                  value={scheduleForm.mealType}
                  onChange={(e) => setScheduleForm({ ...scheduleForm, mealType: e.target.value })}
                >
                  <option value="BREAKFAST">Breakfast</option>
                  <option value="LUNCH">Lunch</option>
                  <option value="DINNER">Dinner</option>
                  <option value="SNACKS">Snacks</option>
                </select>
              </div>

              <div className="form-group">
                <label className="form-label">Select Menu Item</label>
                <select 
                  className="form-select"
                  value={scheduleForm.menuItemId}
                  onChange={(e) => setScheduleForm({ ...scheduleForm, menuItemId: e.target.value })}
                  required
                >
                  <option value="">-- Select Item --</option>
                  {menuItems.map(item => (
                    <option key={item.id} value={item.id}>
                      {item.name} ({item.category} - {item.vegetarian ? 'Veg' : 'Non-Veg'})
                    </option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label className="form-label">Special Note (Optional)</label>
                <input 
                  type="text" 
                  className="form-input"
                  placeholder="e.g. Served with Gulab Jamun"
                  value={scheduleForm.specialNote}
                  onChange={(e) => setScheduleForm({ ...scheduleForm, specialNote: e.target.value })}
                />
              </div>

              <div style={{ display: 'flex', gap: '1rem', marginTop: '1.5rem' }}>
                <button type="button" className="btn btn-secondary" style={{ flex: 1 }} onClick={() => setShowScheduleModal(false)}>Cancel</button>
                <button type="submit" className="btn btn-primary" style={{ flex: 1 }}>Publish Meal</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* MODAL: Add Menu Item */}
      {showItemModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div className="modal-header">
              <h3 className="modal-title">Add Menu Item to Master Catalog</h3>
              <button className="close-btn" onClick={() => setShowItemModal(false)}>✕</button>
            </div>

            <form onSubmit={handleCreateMenuItem}>
              <div className="form-group">
                <label className="form-label">Item Name</label>
                <input 
                  type="text" 
                  className="form-input" 
                  placeholder="e.g. Masala Dosa"
                  value={itemForm.name}
                  onChange={(e) => setItemForm({ ...itemForm, name: e.target.value })}
                  required 
                />
              </div>

              <div className="form-group">
                <label className="form-label">Category</label>
                <input 
                  type="text" 
                  className="form-input"
                  placeholder="e.g. South Indian, North Indian, Dessert"
                  value={itemForm.category}
                  onChange={(e) => setItemForm({ ...itemForm, category: e.target.value })}
                  required 
                />
              </div>

              <div className="form-group">
                <label className="form-label">Dietary Type</label>
                <select 
                  className="form-select"
                  value={itemForm.vegetarian}
                  onChange={(e) => setItemForm({ ...itemForm, vegetarian: e.target.value === 'true' })}
                >
                  <option value="true">Vegetarian 🌱</option>
                  <option value="false">Non-Vegetarian 🍗</option>
                </select>
              </div>

              <div className="form-group">
                <label className="form-label">Description</label>
                <textarea 
                  className="form-textarea" 
                  rows={3}
                  placeholder="Ingredients and description..."
                  value={itemForm.description}
                  onChange={(e) => setItemForm({ ...itemForm, description: e.target.value })}
                />
              </div>

              <div style={{ display: 'flex', gap: '1rem', marginTop: '1.5rem' }}>
                <button type="button" className="btn btn-secondary" style={{ flex: 1 }} onClick={() => setShowItemModal(false)}>Cancel</button>
                <button type="submit" className="btn btn-primary" style={{ flex: 1 }}>Save Item</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* MODAL: Register Resident */}
      {showResidentModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div className="modal-header">
              <h3 className="modal-title">Register New Resident</h3>
              <button className="close-btn" onClick={() => setShowResidentModal(false)}>✕</button>
            </div>

            <form onSubmit={handleCreateResident}>
              <div className="form-group">
                <label className="form-label">Full Name</label>
                <input 
                  type="text" 
                  className="form-input" 
                  placeholder="e.g. Rahul Sharma"
                  value={residentForm.name}
                  onChange={(e) => setResidentForm({ ...residentForm, name: e.target.value })}
                  required 
                />
              </div>

              <div className="form-group">
                <label className="form-label">Roll / Register Number</label>
                <input 
                  type="text" 
                  className="form-input" 
                  placeholder="e.g. 21CS045"
                  value={residentForm.rollNumber}
                  onChange={(e) => setResidentForm({ ...residentForm, rollNumber: e.target.value })}
                  required 
                />
              </div>

              <div className="form-group">
                <label className="form-label">Room Number</label>
                <input 
                  type="text" 
                  className="form-input" 
                  placeholder="e.g. B-304"
                  value={residentForm.roomNumber}
                  onChange={(e) => setResidentForm({ ...residentForm, roomNumber: e.target.value })}
                  required 
                />
              </div>

              <div className="form-group">
                <label className="form-label">Hostel Block</label>
                <input 
                  type="text" 
                  className="form-input" 
                  placeholder="e.g. Boys Block B"
                  value={residentForm.hostelBlock}
                  onChange={(e) => setResidentForm({ ...residentForm, hostelBlock: e.target.value })}
                  required 
                />
              </div>

              <div className="form-group">
                <label className="form-label">Email</label>
                <input 
                  type="email" 
                  className="form-input" 
                  placeholder="student@sece.ac.in"
                  value={residentForm.email}
                  onChange={(e) => setResidentForm({ ...residentForm, email: e.target.value })}
                />
              </div>

              <div style={{ display: 'flex', gap: '1rem', marginTop: '1.5rem' }}>
                <button type="button" className="btn btn-secondary" style={{ flex: 1 }} onClick={() => setShowResidentModal(false)}>Cancel</button>
                <button type="submit" className="btn btn-primary" style={{ flex: 1 }}>Register</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
