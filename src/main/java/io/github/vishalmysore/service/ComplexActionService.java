package io.github.vishalmysore.service;

import com.t4a.annotations.Action;
import com.t4a.annotations.Agent;
import org.springframework.stereotype.Service;

@Service
@Agent(groupName = "customer support", groupDescription = "actions related to customer support")
public class ComplexActionService  {

    public static int COUNTER = 0;
    public ComplexActionService() {
        COUNTER++;
    }
    @Action(description = "Customer has problem create ticket for him")
    public String computerRepair(Customer customer) {
        return customer.toString();
    }
}
