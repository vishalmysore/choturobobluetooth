package io.github.vishalmysore.service;

import com.t4a.annotations.Action;
import com.t4a.annotations.Agent;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

@Service
@Log
@Agent
public class GymService {

    @Action(description = "Add to my gym schedule")
    public String addToMyGymSchedule(MyGymSchedule gymSchedule, String myName) {
        log.info("This is my gym service");
        return "This is my gym service";
    }
}
