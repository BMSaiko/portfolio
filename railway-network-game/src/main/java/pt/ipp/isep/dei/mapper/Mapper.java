package pt.ipp.isep.dei.mapper;

import java.util.List;

public interface Mapper <T> {
    T toDTO(T type);
    List<T> toDTOList(List<T> types);
}
