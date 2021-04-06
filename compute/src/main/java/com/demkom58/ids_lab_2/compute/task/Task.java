package com.demkom58.ids_lab_2.compute.task;

import com.demkom58.ids_lab_2.compute.util.Opt;

import java.io.Serializable;

public interface Task  extends Serializable {
    Opt<String> execute();
}
