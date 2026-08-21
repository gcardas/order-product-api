package cardas.gabriel.orderproductapi.query.sort;


import org.junit.jupiter.api.Test;

import java.util.List;

class SortParamParserSanityTest {

    @Test
    void printsParsedSortInstructions() {
        List<SortInstruction> instructions = SortParamParser.parse("price:desc,name:asc");
        instructions.forEach(i -> System.out.println(i.field() + " " + i.direction()));

        System.out.println("Resulting Sort: " + SortInstructions.toSort(instructions));

        System.out.println("Empty input -> " + SortParamParser.parse(""));
    }
}
