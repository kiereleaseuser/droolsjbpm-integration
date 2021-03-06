/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.container.spring.beans;

import java.util.Map;

import org.drools.core.SessionConfiguration;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.WorkingMemoryEventListener;
import org.kie.internal.runtime.StatelessKnowledgeSession;
import org.kie.api.runtime.CommandExecutor;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.api.runtime.process.WorkItemHandler;

public class StatelessKnowledgeSessionBeanFactory extends AbstractKnowledgeSessionBeanFactory {
    private StatelessKieSession ksession;

    public Class<StatelessKnowledgeSession> getObjectType() {
        return StatelessKnowledgeSession.class;
    }

    @Override
    protected CommandExecutor getCommandExecutor() {
        return ksession;
    }

    @Override
    protected void internalAfterPropertiesSet() {
        if ( getConf() != null && getWorkItems() != null && !getWorkItems().isEmpty() ) {
            Map<String, WorkItemHandler> map = ((SessionConfiguration) getConf()).getWorkItemHandlers();
            map.putAll( getWorkItems() );
        }

//        if ( this.kagent != null ) {
//            ksession = this.kagent.newStatelessKnowledgeSession( getConf() );
//        } else {
            ksession = getKbase().newStatelessKieSession( getConf() );
//        }

        if ( getNode() != null ) {
            getNode().set( getName(),
                           this.ksession );
        }

        // Additions for JIRA JBRULES-3076
        for (AgendaEventListener agendaEventListener : getAgendaEventListeners()) {
            ksession.addEventListener(agendaEventListener);
        }
        for (ProcessEventListener processEventListener :getProcessEventListeners()) {
            ksession.addEventListener(processEventListener);
        }
        for (WorkingMemoryEventListener workingMemoryEventListener :getWorkingMemoryEventListeners()) {
            ksession.addEventListener(workingMemoryEventListener);
        }
        // End of Additions for JIRA JBRULES-3076

        attachLoggers(ksession);
    }
}
