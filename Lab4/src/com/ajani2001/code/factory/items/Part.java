package com.ajani2001.code.factory.items;

public class Part {
    final String supplierName;
    final long id;

    public Part(String supplierName, long id) {
        this.supplierName = supplierName;
        this.id = id;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public long getId() {
        return id;
    }
}
