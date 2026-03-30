package com.scrip.practice.Services;

import com.scrip.practice.Models.Identity;
import com.scrip.practice.Repositories.IdentityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IdentityService {

    @Autowired
    private IdentityRepository repository;

    public List<Identity> getAll() {
        return repository.findAll();
    }

    public Identity getById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public Identity getActive() {
        return repository.findByActiveTrue().orElse(null);
    }

    @Transactional
    public void save(Identity identity) {
        // Si el nuevo registro viene como activo, desactivamos todos primero
        if (identity.isActive()) {
            repository.deactivateAll();
        }
        repository.save(identity);
    }

    @Transactional
    public void activate(Integer id) {
        repository.deactivateAll();
        Identity identity = getById(id);
        if (identity != null) {
            identity.setActive(true);
            repository.save(identity);
        }
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }
}