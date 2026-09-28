package com.sece.expert.service;

import com.sece.expert.entity.MenuItem;
import com.sece.expert.exception.ResourceNotFoundException;
import com.sece.expert.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;

    @Autowired
    public MenuItemService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    public MenuItem createMenuItem(MenuItem menuItem) {
        return menuItemRepository.save(menuItem);
    }

    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    public MenuItem getMenuItemById(Long id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem not found with id: " + id));
    }

    public MenuItem updateMenuItem(Long id, MenuItem updated) {
        MenuItem existing = getMenuItemById(id);
        existing.setName(updated.getName());
        existing.setCategory(updated.getCategory());
        existing.setVegetarian(updated.isVegetarian());
        existing.setDescription(updated.getDescription());
        return menuItemRepository.save(existing);
    }

    public void deleteMenuItem(Long id) {
        if (!menuItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("MenuItem not found with id: " + id);
        }
        menuItemRepository.deleteById(id);
    }
}
