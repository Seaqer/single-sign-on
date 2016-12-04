package interfaces.repository;


import exceptions.SSOException;

import java.util.List;

public interface Repository<T> {
    T addElement(T element) throws SSOException;

    long updateElement(T element) throws SSOException;

    long deleteElement(T element) throws SSOException;

    List<T> getElements(T element) throws SSOException;
}
