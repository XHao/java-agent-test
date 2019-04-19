package io.xhao.javaagent;

import com.beust.jcommander.Parameter;

import lombok.Data;

/**
 * Args
 */
@Data
public class Args {

    @Parameter(names = { "-p", "--pid" }, order = 0, description = "进程号", required = true)
    private String pid;

    @Parameter(names = { "-cl", "--classes" }, order = 1, description = "修改的类", required = true)
    private String classes;

}