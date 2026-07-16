package com.scrip.mscatalogo.service;

import com.scrip.mscatalogo.dto.BookDto;
import com.scrip.mscatalogo.dto.BookRequest;
import com.scrip.mscatalogo.entity.Libro;
import com.scrip.mscatalogo.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<BookDto> listar(String busqueda, String categoria) {
        List<Libro> libros = (busqueda == null || busqueda.isBlank())
                ? bookRepository.findByActivoTrue()
                : bookRepository.buscar(busqueda);

        return libros.stream()
                .filter(libro -> categoria == null || categoria.isBlank()
                        || categoria.equalsIgnoreCase(libro.getCategoria()))
                .map(this::toDto)
                .toList();
    }

    public BookDto obtener(UUID id) {
        return toDto(buscarPorId(id));
    }

    public List<String> listarCategorias() {
        return bookRepository.buscarCategorias();
    }

    public BookDto crear(BookRequest request) {
        bookRepository.findByIsbn(request.isbn()).ifPresent(libro -> {
            throw new IllegalArgumentException("Ya existe un libro con ese ISBN");
        });

        Libro libro = Libro.builder()
                .titulo(request.titulo())
                .isbn(request.isbn())
                .autor(request.autor())
                .categoria(request.categoria())
                .stock(request.stock())
                .build();

        return toDto(bookRepository.save(libro));
    }

    public BookDto actualizar(UUID id, BookRequest request) {
        Libro libro = buscarPorId(id);
        libro.setTitulo(request.titulo());
        libro.setIsbn(request.isbn());
        libro.setAutor(request.autor());
        libro.setCategoria(request.categoria());
        libro.setStock(request.stock());
        return toDto(bookRepository.save(libro));
    }

    public void eliminar(UUID id) {
        Libro libro = buscarPorId(id);
        libro.setActivo(false);
        bookRepository.save(libro);
    }

    public BookDto reservarStock(UUID id, int cantidad) {
        Libro libro = buscarPorId(id);
        int disponible = libro.getStock() - libro.getStockReservado();
        if (disponible < cantidad) {
            throw new IllegalArgumentException("No hay stock disponible suficiente");
        }
        libro.setStockReservado(libro.getStockReservado() + cantidad);
        return toDto(bookRepository.save(libro));
    }

    public BookDto liberarStock(UUID id, int cantidad) {
        Libro libro = buscarPorId(id);
        libro.setStockReservado(Math.max(0, libro.getStockReservado() - cantidad));
        return toDto(bookRepository.save(libro));
    }

    public BookDto subirPortada(UUID id, byte[] imagen) {
        if (imagen == null || imagen.length == 0) {
            throw new IllegalArgumentException("La imagen esta vacia");
        }
        if (imagen.length > MAX_PORTADA_BYTES) {
            throw new IllegalArgumentException("La imagen no puede pesar mas de 2MB");
        }
        if (!esPng(imagen)) {
            throw new IllegalArgumentException("La imagen debe ser un PNG valido");
        }

        Libro libro = buscarPorId(id);
        libro.setPortada(imagen);
        return toDto(bookRepository.save(libro));
    }

    public byte[] obtenerPortada(UUID id) {
        byte[] portada = buscarPorId(id).getPortada();
        if (portada == null) {
            throw new NoSuchElementException("Este libro no tiene portada");
        }
        return portada;
    }

    private static final int MAX_PORTADA_BYTES = 2 * 1024 * 1024;
    private static final byte[] FIRMA_PNG = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};

    private boolean esPng(byte[] imagen) {
        if (imagen.length < FIRMA_PNG.length) {
            return false;
        }
        for (int i = 0; i < FIRMA_PNG.length; i++) {
            if (imagen[i] != FIRMA_PNG[i]) {
                return false;
            }
        }
        return true;
    }

    private Libro buscarPorId(UUID id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Libro no encontrado"));
    }

    private BookDto toDto(Libro libro) {
        return new BookDto(
                libro.getId(),
                libro.getTitulo(),
                libro.getIsbn(),
                libro.getAutor(),
                libro.getCategoria(),
                libro.getStock(),
                libro.getStockReservado(),
                libro.getActivo(),
                libro.getPortada() != null
        );
    }
}
