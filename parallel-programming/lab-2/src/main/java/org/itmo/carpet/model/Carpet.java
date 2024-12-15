package org.itmo.carpet.model;

import java.util.Set;

/**
 * Ковёр Серпинского.
 *
 * @param size            длина стороны ковра
 * @param internalSquares вырезанные квадраты
 */
public record Carpet(int size, Set<Square> internalSquares) {
}
