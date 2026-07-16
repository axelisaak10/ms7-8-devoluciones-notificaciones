package com.scrip.mscatalogo.repository;

import com.scrip.mscatalogo.entity.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Libro, UUID> {

    Optional<Libro> findByIsbn(String isbn);

    List<Libro> findByActivoTrue();

    @Query("""
            SELECT l FROM Libro l
            WHERE l.activo = true
            AND (LOWER(l.titulo) LIKE LOWER(CONCAT('%', :busqueda, '%'))
                 OR LOWER(l.isbn) LIKE LOWER(CONCAT('%', :busqueda, '%')))
            """)
    List<Libro> buscar(@Param("busqueda") String busqueda);

    @Query("""
            SELECT DISTINCT l.categoria FROM Libro l
            WHERE l.activo = true AND l.categoria IS NOT NULL
            ORDER BY l.categoria
            """)
    List<String> buscarCategorias();
}
