package com.demkom58.ids_lab_2.client;

import com.demkom58.ids_lab_2.compute.task.Task;
import com.demkom58.ids_lab_2.compute.util.Opt;
import lombok.SneakyThrows;

import javax.xml.bind.DatatypeConverter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;

public class BruteforceMd5 implements Task {
    private final int length;
    private final String[] slice;
    private final String[] range;
    private final String target;
    private final boolean[] read;

    @SneakyThrows
    public BruteforceMd5(String[] slice, String[] range, String target) {
        if (slice.length != 2) throw new IllegalArgumentException("Slice should contain two elements!");
        if (range.length != 2) throw new IllegalArgumentException("Range should contain two elements!");
        if (slice[0].length() != slice[1].length()) throw new IllegalArgumentException("Start and end of slice should be same length!");
        if (range[0].length() != range[1].length()) throw new IllegalArgumentException("Start and end of range should be same length!");

        this.length = slice[0].length();
        this.slice = slice;
        this.range = range;
        this.target = target.toUpperCase();
        this.read = new boolean[length];
    }

    @Override
    @SneakyThrows
    public Opt<String> execute() {
        final OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream("table.txt"));

        final Opt<String> md5
                = bruteforce(out, MessageDigest.getInstance("MD5"), new StringBuilder(slice[0]), 0, target);
        out.flush();
        out.close();

        if (md5.isPresent() && md5.get().equals(""))
            return Opt.empty();

        return md5;
    }

    @SneakyThrows
    private Opt<String> bruteforce(OutputStreamWriter out, MessageDigest algo, StringBuilder builder, int i, String target) {
        final char s = range[0].charAt(i);
        final char e = range[1].charAt(i);

        for (char c = s; c <= e; c++) {
            if (!read[i]) {
                c = slice[0].charAt(i);
                read[i] = true;
            }

            builder.setCharAt(i, c);

            if (i < length - 1) {
                final Opt<String> result = bruteforce(out, algo, builder, i + 1, target);
                if (result.isPresent())
                    return result;
                else
                    continue;
            }

            final String comb = builder.toString();
            final String md5 = DatatypeConverter.printHexBinary(algo.digest(comb.getBytes()));
            out.write(comb + " : " + i + " : " + md5 + "\n");

            if (target.equals(md5)) return Opt.of(comb);
            if (comb.equals(slice[1])) return Opt.of("");
        }

        return Opt.empty();
    }

}
