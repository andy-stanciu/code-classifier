package me.andystanciu.parser.converter;

public record Feature(int nodeType,  // type of AST node
                      int type,      // actual variable type, or 0 if not present
                      int value) {   // identifier symbol or value, or 0 if not present
    @Override
    public String toString() {
        return nodeType + " " + type + " " + value;
    }
}
