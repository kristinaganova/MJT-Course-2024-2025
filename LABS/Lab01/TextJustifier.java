public class TextJustifier {
    public static String[] justifyText(String[] words, int maxWidth) {
        if (words.length == 0) {
            return new String[0];
        }

        String[] result = new String[words.length];
        int lineCount = 0;

        String[] row = new String[words.length];
        int rowLetters = 0;
        int rowSize = 0;

        for (int i = 0; i < words.length; i++) {
            String word = words[i];

            if (rowLetters + rowSize + word.length() > maxWidth) {
                result[lineCount++] = justifyRow(row, rowSize, rowLetters, maxWidth);
                rowLetters = 0;
                rowSize = 0;
            }

            row[rowSize++] = word;
            rowLetters += word.length();
        }

        result[lineCount++] = leftJustifyRow(row, rowSize, rowLetters, maxWidth);

        String[] finalResult = new String[lineCount];
        System.arraycopy(result, 0, finalResult, 0, lineCount);
        return finalResult;
    }

    private static String justifyRow(String[] row, int rowSize, int rowLetters, int maxWidth) {
        int spacesToAdd = maxWidth - rowLetters;
        if (rowSize == 1) {
            return justifySingleWordRow(row[0], spacesToAdd);
        } else {
            return justifyMultipleWordsRow(row, rowSize, spacesToAdd);
        }
    }

    private static String justifySingleWordRow(String word, int spacesToAdd) {
        StringBuilder sb = new StringBuilder(word);
        for (int i = 0; i < spacesToAdd; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }

    private static String justifyMultipleWordsRow(String[] row, int rowSize, int spacesToAdd) {
        StringBuilder sb = new StringBuilder();
        int spaceBetweenWords = spacesToAdd / (rowSize - 1);
        int extraSpaces = spacesToAdd % (rowSize - 1);

        for (int i = 0; i < rowSize; i++) {
            sb.append(row[i]);
            if (i != rowSize - 1) {
                for (int j = 0; j < spaceBetweenWords; j++) {
                    sb.append(" ");
                }
                if (extraSpaces > 0) {
                    sb.append(" ");
                    extraSpaces--;
                }
            }
        }
        return sb.toString();
    }

    private static String leftJustifyRow(String[] row, int rowSize, int rowLetters, int maxWidth) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rowSize; i++) {
            sb.append(row[i]);
            if (i != rowSize - 1) {
                sb.append(" ");
            }
        }
        while (sb.length() < maxWidth) {
            sb.append(" ");
        }
        return sb.toString();
    }
}
