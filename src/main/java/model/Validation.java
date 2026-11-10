package model;

final  class Validation {

    private Validation() {}

    public static String requireText( String value, String fieldName ) {

        if ( value == null || value.isBlank() ) {
            throw new IllegalArgumentException( fieldName + " must not be blank");
        }

        if ( value.contains(";") || value.contains("\n") || value.contains("\r") ) { // the save file uses ‘;’ as separator
            throw new IllegalArgumentException( fieldName + " must not contain ';' or line breaks");
        }

        return value;
    }

    public static int requireAtLeast(int number, int minimum, String fieldName) {

        if (number < minimum ) {
            throw new IllegalArgumentException(fieldName + " must be at least " + minimum);
        }
        return number;
    }
}
