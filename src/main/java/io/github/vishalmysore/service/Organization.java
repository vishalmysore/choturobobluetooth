package io.github.vishalmysore.service;

import com.t4a.annotations.ListType;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Organization {
    String name;
    @ListType(Employee.class)
    List<Employee> em;
    @ListType(String.class)
    List<String> locations;
    Customer[] customers;
}
