package com.musicgame.v1.utils;

import com.musicgame.v1.model.Visitor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StatisticUtil {

    private final List<Visitor> visitors = new ArrayList<>();

    public void addVisitor(String sessionId) {
        if (visitors.stream().map(Visitor::getSessionId).noneMatch(visitorSesId -> visitorSesId.equals(sessionId))){
            visitors.add(new Visitor(sessionId));
        }
    }

    public Integer viewVisitors(){
        return visitors.size();
    }

    public List<Visitor> getVisitors() {
        return visitors;
    }
}
