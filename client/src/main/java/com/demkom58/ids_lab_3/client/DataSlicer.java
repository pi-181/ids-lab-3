package com.demkom58.ids_lab_3.client;

import java.util.stream.IntStream;

public class DataSlicer {
    private final int slices;
    private final String start;
    private final String end;

    private int currRange = 1;

    public DataSlicer(int slices, String start, String end) {
        this.slices = slices;
        this.start = start;
        this.end = end;
    }

    public String[] slice() {
        final int total = weight(start, end);
        final int optimalJobWeight = total / slices;
        System.out.println("Optimal job is " + optimalJobWeight + " when total " + total);
        return findRanges(start, end, optimalJobWeight, slices);
    }

    private String[] findRanges(String start, String end, int optimalWeight, int slices) {
        final StringBuilder rangeBuilder = new StringBuilder(start);
        final String[] ranges = new String[slices + 1];

        find(rangeBuilder, 0, optimalWeight, ranges);

        ranges[0] = start;
        ranges[slices] = end;

        this.currRange = 1;

        return ranges;
    }

    private void find(StringBuilder builder, int i, int optimalWeight, String[] ranges) {
        final char s = start.charAt(i);
        final char e = end.charAt(i);

        for (char c = s; c <= e; c++) {
            builder.setCharAt(i, c);
            if (i < end.length() - 1)
                find(builder, i + 1, optimalWeight, ranges);

            if (currRange > slices - 1)
                break;

            final int weight = weight(start, builder);
            if (optimalWeight * currRange - weight < 5000) {
                ranges[currRange] = builder.toString();
                currRange++;
            }
        }
    }

    public static int weight(CharSequence start, CharSequence end) {
        final int[] sizes = new int[start.length()];

        for (int i = 0; i < start.length(); i++)
            sizes[i] = (end.charAt(i) - start.charAt(i)) + 1;

        return IntStream.of(sizes)
                .reduce((left, right) -> left * right)
                .orElse(0);
    }

}
