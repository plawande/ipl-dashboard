package com.plawande.ipldashboard.data;

import com.plawande.ipldashboard.model.Match;
import com.plawande.ipldashboard.repository.MatchRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class MatchDataWriter implements ItemWriter<Match> {

    @Autowired
    private MatchRepository matchRepository;

    @Override
    public void write(List<? extends Match> matchList) throws Exception {
        matchRepository.saveAll(matchList);
    }
}
