package io.github.vishalmysore.service;

import com.t4a.annotations.Action;
import com.t4a.annotations.Agent;


@Agent
public class MyDiaryAction {
    @Action(description = "This is my diary details")
    public MyDiary buildMyDiary(MyDiary diary) {
        //take whatever action you want to take
        return diary;
    }
}
