package com.plawande.ipldashboard.data;

import com.plawande.ipldashboard.model.Team;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public void afterJob(JobExecution jobExecution) {
        if(jobExecution.getStatus() == BatchStatus.COMPLETED) {

            Map<String, Long> matchCountMap1 = em.createQuery("select m.team1 from Match m", String.class)
                    .getResultList()
                    .stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

            Map<String, Long> matchCountMap2 = em.createQuery("select m.team2 from Match m", String.class)
                    .getResultList()
                    .stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

            Map<String, Long> matchCountMap3 = new HashMap<>(matchCountMap1);
            matchCountMap2.forEach((key, value) -> matchCountMap3.merge(key, value, (v1, v2) -> v1+v2));

            List<Team> teams = matchCountMap3.entrySet()
                    .stream()
                    .map(entry -> new Team(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());

            Map<String, Long> winnerCountMap = em.createQuery("select m.matchWinner from Match m", String.class)
                    .getResultList()
                    .stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

            teams.forEach(team -> team.setTotalWins(winnerCountMap.get(team.getTeamName())));
            teams.forEach(team -> em.persist(team));
            System.out.println(teams);
        }
    }
}
