package com.sece.expert.config;

import com.sece.expert.dto.FeedbackRequest;
import com.sece.expert.dto.MealScheduleRequest;
import com.sece.expert.dto.OptOutRequest;
import com.sece.expert.entity.Meal;
import com.sece.expert.entity.MealType;
import com.sece.expert.entity.MenuItem;
import com.sece.expert.entity.Resident;
import com.sece.expert.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ResidentService residentService;
    private final MenuItemService menuItemService;
    private final MealService mealService;
    private final FeedbackService feedbackService;
    private final MealOptOutService mealOptOutService;

    @Autowired
    public DataInitializer(ResidentService residentService,
                           MenuItemService menuItemService,
                           MealService mealService,
                           FeedbackService feedbackService,
                           MealOptOutService mealOptOutService) {
        this.residentService = residentService;
        this.menuItemService = menuItemService;
        this.mealService = mealService;
        this.feedbackService = feedbackService;
        this.mealOptOutService = mealOptOutService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (residentService.getTotalResidentsCount() > 0) {
            return; // Already initialized
        }

        System.out.println(">>> Initializing MessMate Sample Data...");

        // 1. Seed Residents
        Resident r1 = residentService.createResident(new Resident("Rahul Sharma", "21CS045", "B-304", "Boys Block B", "rahul.s@sece.ac.in"));
        Resident r2 = residentService.createResident(new Resident("Priya Patel", "21EC089", "A-102", "Girls Block A", "priya.p@sece.ac.in"));
        Resident r3 = residentService.createResident(new Resident("Anish Kumar", "21ME012", "C-205", "Boys Block C", "anish.k@sece.ac.in"));
        Resident r4 = residentService.createResident(new Resident("Sneha Reddy", "21IT110", "A-204", "Girls Block A", "sneha.r@sece.ac.in"));
        Resident r5 = residentService.createResident(new Resident("Vikram Singh", "21EE055", "B-108", "Boys Block B", "vikram.s@sece.ac.in"));

        // 2. Seed Menu Items
        MenuItem item1 = menuItemService.createMenuItem(new MenuItem("Masala Dosa & Coconut Chutney", "South Indian", true, "Crispy rice crepe filled with spiced potato curry served with fresh coconut chutney and sambar."));
        MenuItem item2 = menuItemService.createMenuItem(new MenuItem("Paneer Butter Masala & Butter Naan", "North Indian", true, "Rich cottage cheese gravy with soft naan."));
        MenuItem item3 = menuItemService.createMenuItem(new MenuItem("Hydrabadi Chicken Biryani", "Biryani", false, "Aromatic basmati rice cooked with succulent chicken and exotic spices."));
        MenuItem item4 = menuItemService.createMenuItem(new MenuItem("Poori Masala & Hot Milk", "Breakfast", true, "Deep-fried puffed bread with potato masala."));
        MenuItem item5 = menuItemService.createMenuItem(new MenuItem("South Indian Veg Meals (Thali)", "South Indian", true, "Rice, Sambar, Rasam, Poriyal, Kootu, Curd & Papad."));
        MenuItem item6 = menuItemService.createMenuItem(new MenuItem("Chapati & Yellow Dal Tadka", "Dinner", true, "Soft whole wheat chapatis served with tempered lentils."));
        MenuItem item7 = menuItemService.createMenuItem(new MenuItem("Samosa & Hot Masala Tea", "Snacks", true, "Crispy evening snack served with mint chutney."));

        // 3. Seed Weekly Meals (Current week)
        LocalDate today = LocalDate.now();
        LocalDate monday = today.minusDays(today.getDayOfWeek().getValue() - 1);

        Meal m1 = mealService.scheduleMeal(new MealScheduleRequest(monday, MealType.BREAKFAST, item1.getId(), "Chef's Special Chutney"));
        Meal m2 = mealService.scheduleMeal(new MealScheduleRequest(monday, MealType.LUNCH, item5.getId(), "Includes Sweet Pongal"));
        Meal m3 = mealService.scheduleMeal(new MealScheduleRequest(monday, MealType.DINNER, item2.getId(), "Served from 7:30 PM"));

        Meal m4 = mealService.scheduleMeal(new MealScheduleRequest(monday.plusDays(1), MealType.BREAKFAST, item4.getId(), "Hot crisp pooris"));
        Meal m5 = mealService.scheduleMeal(new MealScheduleRequest(monday.plusDays(1), MealType.LUNCH, item3.getId(), "Veg option Paneer Biryani"));
        Meal m6 = mealService.scheduleMeal(new MealScheduleRequest(monday.plusDays(1), MealType.DINNER, item6.getId(), "Light dinner meal"));

        Meal m7 = mealService.scheduleMeal(new MealScheduleRequest(today, MealType.BREAKFAST, item1.getId(), "Fresh crisp dosas"));
        Meal m8 = mealService.scheduleMeal(new MealScheduleRequest(today, MealType.LUNCH, item5.getId(), "Standard Thali"));
        Meal m9 = mealService.scheduleMeal(new MealScheduleRequest(today, MealType.DINNER, item2.getId(), "Paneer special night"));

        // 4. Seed Sample Feedbacks
        try {
            feedbackService.submitFeedback(new FeedbackRequest(r1.getId(), m1.getId(), 5, "Dosa was super crisp and chutney was fresh!"));
            feedbackService.submitFeedback(new FeedbackRequest(r2.getId(), m1.getId(), 4, "Very tasty breakfast."));
            feedbackService.submitFeedback(new FeedbackRequest(r3.getId(), m2.getId(), 2, "Sambar was cold and watery today. Need improvement."));
            feedbackService.submitFeedback(new FeedbackRequest(r4.getId(), m2.getId(), 2, "Vegetable curry lacked salt."));
            feedbackService.submitFeedback(new FeedbackRequest(r1.getId(), m7.getId(), 5, "Excellent dosa today!"));
            feedbackService.submitFeedback(new FeedbackRequest(r5.getId(), m7.getId(), 4, "Good quantity and quality."));
        } catch (Exception e) {
            System.err.println("Seed feedback warning: " + e.getMessage());
        }

        // 5. Seed Opt-Outs
        try {
            mealOptOutService.registerOptOut(new OptOutRequest(r3.getId(), today, MealType.DINNER, "Going out with family"));
            mealOptOutService.registerOptOut(new OptOutRequest(r4.getId(), monday.plusDays(1), null, "Weekend home visit"));
        } catch (Exception e) {
            System.err.println("Seed opt-out warning: " + e.getMessage());
        }

        System.out.println(">>> MessMate Sample Data Initialized Successfully!");
    }
}
