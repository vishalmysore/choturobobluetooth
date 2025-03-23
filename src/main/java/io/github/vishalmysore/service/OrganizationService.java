package io.github.vishalmysore.service;

import com.t4a.annotations.Action;
import com.t4a.annotations.Agent;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

@Service
@Log
@Agent
public class OrganizationService {

    @Action(description = "Add to my organization")
    public String addToMyOrganization(Organization organization, String newOrgName) {
        log.info("This is myn ew org name: "+newOrgName);
        return "This is my org service "+organization;
    }
    @Action(description = "Add to my Dictionary")
    public String addToMyDictionary(Dictionary dictionary, String newWord) {
        log.info("This is Dictionary: "+newWord);
        return "This is my org service "+newWord;
    }

}
