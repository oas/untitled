package org.example;

import org.drools.core.impl.RuleBaseFactory;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.definition.KiePackage;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        KnowledgeBuilderConfiguration knowledgeBuilderConfiguration = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
        KieBaseConfiguration kieBaseConfiguration = RuleBaseFactory.newKnowledgeBaseConfiguration();
        KieBase kbase = loadKnowledgeBase(knowledgeBuilderConfiguration, kieBaseConfiguration, "test_LogicalInsertions2.drl");
        KieSession ksession = kbase.newKieSession();

        try {
            List<?> events = new ArrayList<>();

            ksession.setGlobal("events", events);

            Sensor sensor = new Sensor(200, 200);

            ksession.insert(sensor);
            ksession.fireAllRules();

            Collection<?> list = ksession.getObjects();

            for (Object o : list) {
                System.out.println(o);
            }

            for (Object o : events) {
                System.out.println(o);
            }
        } finally {
            ksession.dispose();
        }
    }

    protected static KieBase loadKnowledgeBase(KnowledgeBuilderConfiguration kbuilderConf, KieBaseConfiguration kbaseConf, String... classPathResources) {
        Collection<KiePackage> knowledgePackages = loadKnowledgePackages(kbuilderConf, classPathResources);

        if (kbaseConf == null) {
            kbaseConf = RuleBaseFactory.newKnowledgeBaseConfiguration();
        }
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(RuleBaseFactory.newRuleBase(kbaseConf));
        kbase.addPackages(knowledgePackages);
        return kbase;
    }

    public static Collection<KiePackage> loadKnowledgePackages(KnowledgeBuilderConfiguration kbuilderConf, String... classPathResources) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(kbuilderConf);
        for (String classPathResource : classPathResources) {
            kbuilder.add(ResourceFactory.newClassPathResource(classPathResource, Main.class), ResourceType.DRL);
        }

        if (kbuilder.hasErrors()) {
            throw new RuntimeException(kbuilder.getErrors().toString());
        }

        return kbuilder.getKnowledgePackages();
    }
}
