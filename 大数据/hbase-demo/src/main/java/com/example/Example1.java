package com.example;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class Example1 {
    static Configuration cfg = HBaseConfiguration.create();
    static {
        cfg.set("hbase.zookeeper.quorum", "192.168.93.131");
        cfg.set("hbase.zookeeper.property.clientPort", "2181");
    }

    private static final String TABLE_NAME = "MY_TABLE_NAME_TOO";
    private static final String CF_DEFAULT = "DEFAULT_COLUMN_FAMILY";

    public static void checkExist(Admin admin, TableDescriptor tableDescriptor) throws IOException {
        if (admin.tableExists(tableDescriptor.getTableName())) {
            admin.disableTable(tableDescriptor.getTableName());
            admin.deleteTable(tableDescriptor.getTableName());
        }
        admin.createTable(tableDescriptor);
    }

    public static void createSchemaTables(Configuration config) throws IOException {
        try (Connection connection = ConnectionFactory.createConnection(config);
             Admin admin = connection.getAdmin()) {

            TableName tablename = TableName.valueOf(TABLE_NAME);//定义表名
            TableDescriptorBuilder tableDescriptorBuilder = TableDescriptorBuilder.newBuilder(tablename);

            //定义列族
            ColumnFamilyDescriptor columnFamily = ColumnFamilyDescriptorBuilder.newBuilder(CF_DEFAULT.getBytes()).build();
            tableDescriptorBuilder.setColumnFamily(columnFamily);

            System.out.print("Creating table. ");
            checkExist(admin, tableDescriptorBuilder.build());
            System.out.println(" Done.");
        }
    }

    public static void main(String... args) throws IOException {
        createSchemaTables(cfg);
    }
}
