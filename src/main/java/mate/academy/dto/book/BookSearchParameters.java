package mate.academy.dto.book;

import java.math.BigDecimal;

public record BookSearchParameters(String[] titles,
                                   String[] authors,
                                   String[] isbns,
                                   BigDecimal[] prices,
                                   String[] descriptions,
                                   String[] coverImages
) {
}
