package com.demkom58.ids_lab_2.compute.task;

import java.io.Serializable;

public interface Task  extends Serializable {
    Object execute();
}
