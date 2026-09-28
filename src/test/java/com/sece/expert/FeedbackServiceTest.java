package com.sece.expert;

import com.sece.expert.dto.FeedbackRequest;
import com.sece.expert.entity.*;
import com.sece.expert.exception.DuplicateFeedbackException;
import com.sece.expert.repository.FeedbackRepository;
import com.sece.expert.repository.MealRepository;
import com.sece.expert.repository.ResidentRepository;
import com.sece.expert.service.FeedbackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FeedbackServiceTest {

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private ResidentRepository residentRepository;

    @Mock
    private MealRepository mealRepository;

    @InjectMocks
    private FeedbackService feedbackService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSubmitFeedback_Success() {
        Resident resident = new Resident(1L, "Rahul", "21CS01", "101", "A", "r@test.com");
        MenuItem item = new MenuItem("Dosa", "South", true, "Crispy");
        Meal meal = new Meal(LocalDate.now(), MealType.BREAKFAST, item, "Note");
        meal.setId(10L);

        when(residentRepository.findById(1L)).thenReturn(Optional.of(resident));
        when(mealRepository.findById(10L)).thenReturn(Optional.of(meal));
        when(feedbackRepository.existsByResidentIdAndMealId(1L, 10L)).thenReturn(false);
        when(feedbackRepository.save(any(Feedback.class))).thenAnswer(i -> i.getArguments()[0]);

        FeedbackRequest request = new FeedbackRequest(1L, 10L, 5, "Great meal!");
        Feedback result = feedbackService.submitFeedback(request);

        assertNotNull(result);
        assertEquals(5, result.getRating());
        assertEquals("Great meal!", result.getComment());
        verify(feedbackRepository, times(1)).save(any(Feedback.class));
    }

    @Test
    void testSubmitFeedback_DuplicateThrowsException() {
        Resident resident = new Resident(1L, "Rahul", "21CS01", "101", "A", "r@test.com");
        MenuItem item = new MenuItem("Dosa", "South", true, "Crispy");
        Meal meal = new Meal(LocalDate.now(), MealType.BREAKFAST, item, "Note");
        meal.setId(10L);

        when(residentRepository.findById(1L)).thenReturn(Optional.of(resident));
        when(mealRepository.findById(10L)).thenReturn(Optional.of(meal));
        when(feedbackRepository.existsByResidentIdAndMealId(1L, 10L)).thenReturn(true);

        FeedbackRequest request = new FeedbackRequest(1L, 10L, 4, "Second attempt");

        DuplicateFeedbackException exception = assertThrows(
                DuplicateFeedbackException.class,
                () -> feedbackService.submitFeedback(request)
        );

        assertTrue(exception.getMessage().contains("already submitted feedback"));
        verify(feedbackRepository, never()).save(any());
    }
}
