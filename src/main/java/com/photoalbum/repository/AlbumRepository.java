package com.photoalbum.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.photoalbum.model.Album;
import com.photoalbum.model.User;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
    List<Album> findByUser(User user);
    
    List<Album> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    
    long countByUser(User user);
} 