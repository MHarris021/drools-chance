/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.pmml.pmml_4_1.predictive.models;


import junit.framework.Assert;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.ObjectFilter;
import org.drools.agent.ChangeSetHelperImpl;
import org.drools.agent.KnowledgeAgent;
import org.drools.agent.KnowledgeAgentConfiguration;
import org.drools.agent.KnowledgeAgentFactory;
import org.drools.agent.conf.NewInstanceOption;
import org.drools.agent.conf.UseKnowledgeBaseClassloaderOption;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.ClassPathResource;
import org.drools.pmml.pmml_4_1.DroolsAbstractPMMLTest;
import org.drools.pmml.pmml_4_1.ModelMarker;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CleanupTest extends DroolsAbstractPMMLTest {


    private static final String source1 = "org/drools/pmml/pmml_4_1//test_ann_iris_prediction.xml";
    private static final String source2 = "org/drools/pmml/pmml_4_1//test_tree_simple.xml";
    private static final String source3 = "org/drools/pmml/pmml_4_1//test_regression.xml";
    private static final String source4 = "org/drools/pmml/pmml_4_1//test_clustering.xml";
    private static final String source5 = "org/drools/pmml/pmml_4_1//test_svm.xml";
    private static final String source6 = "org/drools/pmml/pmml_4_1//test_scorecard.xml";

    private static final String source9 = "org/drools/pmml/pmml_4_1//mock_cold.xml";

    private static final String packageName = "org.drools.pmml.pmml_4_1.test";




    @Test
    public void testCleanupANN() {
        StatefulKnowledgeSession kSession = loadModel( source1 );
        assertTrue( kSession.getObjects().size() > 0 );

        Collection qres = getModelMarker( kSession, "Neuiris" );
        assertEquals( 1, qres.size() );

        kSession.getWorkingMemoryEntryPoint( "enable_Neuiris" ).insert( Boolean.FALSE );
        kSession.fireAllRules();

        System.err.println(reportWMObjects(kSession));

        assertEquals( 2, kSession.getObjects().size() );
        kSession.dispose();
    }

    private Collection getModelMarker( final StatefulKnowledgeSession kSession, final String modelName ) {
        return kSession.getObjects( new ObjectFilter() {
            public boolean accept( Object o ) {
                return o instanceof ModelMarker && modelName.equals( ((ModelMarker) o).getModelName() );
            }
        } );
    }


    @Test
    public void testReenableANN() {
        setKSession( loadModel( source1 ) );
        assertTrue( getKSession().getObjects().size() > 0 );

        Collection qres = getModelMarker( getKSession(), "Neuiris" );
        assertEquals( 1, qres.size() );

        getKSession().getWorkingMemoryEntryPoint( "enable_Neuiris" ).insert( Boolean.FALSE );

        getKSession().fireAllRules();

        System.err.println(reportWMObjects(getKSession()));

        assertEquals( 2, getKSession().getObjects().size() );

        getKSession().getWorkingMemoryEntryPoint( "enable_Neuiris" ).insert( Boolean.TRUE );
        getKSession().fireAllRules();

        System.err.println( reportWMObjects(getKSession()) );

        getKSession().getWorkingMemoryEntryPoint("in_PetalNum").insert(101);
        getKSession().getWorkingMemoryEntryPoint("in_PetalWid").insert(2);
        getKSession().getWorkingMemoryEntryPoint("in_Species").insert("virginica");
        getKSession().getWorkingMemoryEntryPoint("in_SepalWid").insert(30);
        getKSession().fireAllRules();


        System.err.println(reportWMObjects(getKSession()));

        Assert.assertEquals(24.0, queryIntegerField("OutSepLen", "Neuiris"));

        assertEquals( 62, getKSession().getObjects().size() );

        getKSession().dispose();
    }



    @Test
    public void testCleanupDT() {
        StatefulKnowledgeSession kSession = loadModel( source2 );
        assertTrue( kSession.getObjects().size() > 0 );

        Collection qres = getModelMarker( kSession, "TreeTest" );
        assertEquals( 1, qres.size() );

        kSession.getWorkingMemoryEntryPoint( "enable_TreeTest" ).insert(Boolean.FALSE);
        kSession.fireAllRules();

        System.err.println(reportWMObjects(kSession));

        assertEquals( 1, kSession.getObjects().size() );

        kSession.dispose();
    }

    @Test
    public void testCleanupRegression() {
        StatefulKnowledgeSession kSession = loadModel( source3 );
        assertTrue( kSession.getObjects().size() > 0 );

        Collection qres = getModelMarker( kSession, "LinReg" );
        assertEquals( 1, qres.size() );

        kSession.getWorkingMemoryEntryPoint( "enable_LinReg" ).insert(Boolean.FALSE);
        kSession.fireAllRules();

        System.err.println(reportWMObjects(kSession));

        assertEquals( 1, kSession.getObjects().size() );

        kSession.dispose();
    }

    @Test
    public void testCleanupClustering() {
        StatefulKnowledgeSession kSession = loadModel( source4 );
        assertTrue( kSession.getObjects().size() > 0 );

        Collection qres = getModelMarker( kSession, "CenterClustering" );
        assertEquals( 1, qres.size() );

        kSession.getWorkingMemoryEntryPoint( "enable_CenterClustering" ).insert(Boolean.FALSE);
        kSession.fireAllRules();

        System.err.println(reportWMObjects(kSession));

        assertEquals( 1, kSession.getObjects().size() );

        kSession.dispose();
    }

    @Test
    public void testCleanupSVM() {
        StatefulKnowledgeSession kSession = loadModel( source5 );
        assertTrue( kSession.getObjects().size() > 0 );

        Collection qres = getModelMarker( kSession, "SVMXORModel" );
        assertEquals( 1, qres.size() );

        kSession.getWorkingMemoryEntryPoint( "enable_SVMXORModel" ).insert(Boolean.FALSE);
        kSession.fireAllRules();

        System.err.println(reportWMObjects(kSession));

        assertEquals( 1, kSession.getObjects().size() );

        kSession.dispose();
    }

    @Test
    public void testCleanupScorecard() {
        StatefulKnowledgeSession kSession = loadModel( source6 );
        assertTrue( kSession.getObjects().size() > 0 );

        Collection qres = getModelMarker( kSession, "SampleScore" );
        assertEquals( 1, qres.size() );

        kSession.getWorkingMemoryEntryPoint( "enable_SampleScore" ).insert(Boolean.FALSE);
        kSession.fireAllRules();

        System.err.println(reportWMObjects(kSession));

        assertEquals( 1, kSession.getObjects().size() );

        kSession.dispose();
    }



    @Test
    public void testCleanupANNRulesWithIncrementalKA() {

        KnowledgeAgent kAgent = initIncrementalKA();
//        kAgent.setSystemEventListener( new PrintStreamSystemEventListener() );

        KnowledgeBase kBase = kAgent.getKnowledgeBase();
        StatefulKnowledgeSession kSession = kBase.newStatefulKnowledgeSession();

        ChangeSetHelperImpl csAdd = new ChangeSetHelperImpl();
        ClassPathResource res = (ClassPathResource) ResourceFactory.newClassPathResource( source1 );
        res.setResourceType( ResourceType.PMML );
        ClassPathResource res2 = (ClassPathResource) ResourceFactory.newClassPathResource( source9 );
        res2.setResourceType( ResourceType.PMML );
        csAdd.addNewResource(res);
        csAdd.addNewResource( res2 );

        System.out.println( "************************ ADDING resources ");

        kAgent.applyChangeSet( csAdd.getChangeSet() );

        assertTrue( kBase.getKnowledgePackage( packageName ).getRules().size() > 0 );
        kSession.fireAllRules();
        assertTrue( kSession.getObjects().size() > 0 );


        System.out.println( "************************ REMOVING resource 1 ");

        ChangeSetHelperImpl csRem = new ChangeSetHelperImpl();
        csRem.addRemovedResource( res );
        kAgent.applyChangeSet( csRem .getChangeSet() );

        kSession.fireAllRules();

        assertEquals( 40, kBase.getKnowledgePackage( packageName ).getRules().size() );

        System.out.println( "************************ REMOVING resource 2 ");

        ChangeSetHelperImpl csRem2 = new ChangeSetHelperImpl();
        csRem2.addRemovedResource( res2 );
        kAgent.applyChangeSet( csRem2.getChangeSet() );

        kSession.fireAllRules();


        System.out.println(reportWMObjects(kSession));

        assertEquals( 0, kBase.getKnowledgePackage( packageName ).getRules().size() );

        System.err.println( reportWMObjects( kSession ) );
        assertEquals( 0, kSession.getObjects().size() );

        kSession.dispose();
        kAgent.dispose();

    }


    @Test
    public void testCleanupDTRulesWithIncrementalKA() {
        KnowledgeAgent kAgent = initIncrementalKA();
        //        kAgent.setSystemEventListener( new PrintStreamSystemEventListener() );

        KnowledgeBase kBase = kAgent.getKnowledgeBase();
        StatefulKnowledgeSession kSession = kBase.newStatefulKnowledgeSession();

        ChangeSetHelperImpl csAdd = new ChangeSetHelperImpl();
        ClassPathResource res = (ClassPathResource) ResourceFactory.newClassPathResource( source2 );
        res.setResourceType( ResourceType.PMML );
        csAdd.addNewResource(res);

        System.out.println( "************************ ADDING resources ");

        kAgent.applyChangeSet( csAdd.getChangeSet() );

        assertTrue( kBase.getKnowledgePackage( packageName ).getRules().size() > 0 );
        kSession.fireAllRules();
        assertTrue( kSession.getObjects().size() > 0 );


        System.out.println( "************************ REMOVING resource 1 ");

        ChangeSetHelperImpl csRem = new ChangeSetHelperImpl();
        csRem.addRemovedResource( res );
        kAgent.applyChangeSet( csRem .getChangeSet() );

        kSession.fireAllRules();

        assertEquals( 0, kBase.getKnowledgePackage( packageName ).getRules().size() );

        System.err.println( reportWMObjects( kSession ) );
        assertEquals( 0, kSession.getObjects().size() );

        kSession.dispose();
        kAgent.dispose();
    }

    @Test
    public void testCleanupClusteringRulesWithIncrementalKA() {
        KnowledgeAgent kAgent = initIncrementalKA();
        //        kAgent.setSystemEventListener( new PrintStreamSystemEventListener() );

        KnowledgeBase kBase = kAgent.getKnowledgeBase();
        StatefulKnowledgeSession kSession = kBase.newStatefulKnowledgeSession();

        ChangeSetHelperImpl csAdd = new ChangeSetHelperImpl();
        ClassPathResource res = (ClassPathResource) ResourceFactory.newClassPathResource( source3 );
        res.setResourceType( ResourceType.PMML );
        csAdd.addNewResource(res);

        System.out.println( "************************ ADDING resources ");

        kAgent.applyChangeSet( csAdd.getChangeSet() );

        assertTrue( kBase.getKnowledgePackage( packageName ).getRules().size() > 0 );
        kSession.fireAllRules();
        assertTrue( kSession.getObjects().size() > 0 );


        System.out.println( "************************ REMOVING resource 1 ");

        ChangeSetHelperImpl csRem = new ChangeSetHelperImpl();
        csRem.addRemovedResource( res );
        kAgent.applyChangeSet( csRem .getChangeSet() );

        kSession.fireAllRules();

        assertEquals( 0, kBase.getKnowledgePackage( packageName ).getRules().size() );

        System.err.println( reportWMObjects( kSession ) );
        assertEquals( 0, kSession.getObjects().size() );

        kSession.dispose();
        kAgent.dispose();
    }

    @Test
    public void testCleanupRegressionRulesWithIncrementalKA() {
        KnowledgeAgent kAgent = initIncrementalKA();
        //        kAgent.setSystemEventListener( new PrintStreamSystemEventListener() );

        KnowledgeBase kBase = kAgent.getKnowledgeBase();
        StatefulKnowledgeSession kSession = kBase.newStatefulKnowledgeSession();

        ChangeSetHelperImpl csAdd = new ChangeSetHelperImpl();
        ClassPathResource res = (ClassPathResource) ResourceFactory.newClassPathResource( source4 );
        res.setResourceType( ResourceType.PMML );
        csAdd.addNewResource(res);

        System.out.println( "************************ ADDING resources ");

        kAgent.applyChangeSet( csAdd.getChangeSet() );

        assertTrue( kBase.getKnowledgePackage( packageName ).getRules().size() > 0 );
        kSession.fireAllRules();
        assertTrue( kSession.getObjects().size() > 0 );


        System.out.println( "************************ REMOVING resource 1 ");

        ChangeSetHelperImpl csRem = new ChangeSetHelperImpl();
        csRem.addRemovedResource( res );
        kAgent.applyChangeSet( csRem .getChangeSet() );

        kSession.fireAllRules();

        assertEquals( 0, kBase.getKnowledgePackage( packageName ).getRules().size() );

        System.err.println( reportWMObjects( kSession ) );
        assertEquals( 0, kSession.getObjects().size() );

        kSession.dispose();
        kAgent.dispose();
    }

    @Test
    public void testCleanupSVMRulesWithIncrementalKA() {
        KnowledgeAgent kAgent = initIncrementalKA();
        //        kAgent.setSystemEventListener( new PrintStreamSystemEventListener() );

        KnowledgeBase kBase = kAgent.getKnowledgeBase();
        StatefulKnowledgeSession kSession = kBase.newStatefulKnowledgeSession();

        ChangeSetHelperImpl csAdd = new ChangeSetHelperImpl();
        ClassPathResource res = (ClassPathResource) ResourceFactory.newClassPathResource( source5 );
        res.setResourceType( ResourceType.PMML );
        csAdd.addNewResource(res);

        System.out.println( "************************ ADDING resources ");

        kAgent.applyChangeSet( csAdd.getChangeSet() );

        assertTrue( kBase.getKnowledgePackage( packageName ).getRules().size() > 0 );
        kSession.fireAllRules();
        assertTrue( kSession.getObjects().size() > 0 );


        System.out.println( "************************ REMOVING resource 1 ");

        ChangeSetHelperImpl csRem = new ChangeSetHelperImpl();
        csRem.addRemovedResource( res );
        kAgent.applyChangeSet( csRem .getChangeSet() );

        kSession.fireAllRules();

        assertEquals( 0, kBase.getKnowledgePackage( packageName ).getRules().size() );

        System.err.println( reportWMObjects( kSession ) );
        assertEquals( 0, kSession.getObjects().size() );

        kSession.dispose();
        kAgent.dispose();
    }

    @Test
    public void testCleanupScorecardRulesWithIncrementalKA() {
        KnowledgeAgent kAgent = initIncrementalKA();
        //        kAgent.setSystemEventListener( new PrintStreamSystemEventListener() );

        KnowledgeBase kBase = kAgent.getKnowledgeBase();
        StatefulKnowledgeSession kSession = kBase.newStatefulKnowledgeSession();

        ChangeSetHelperImpl csAdd = new ChangeSetHelperImpl();
        ClassPathResource res = (ClassPathResource) ResourceFactory.newClassPathResource( source6 );
        res.setResourceType( ResourceType.PMML );
        csAdd.addNewResource(res);

        System.out.println( "************************ ADDING resources ");

        kAgent.applyChangeSet( csAdd.getChangeSet() );

        assertTrue( kBase.getKnowledgePackage( "org.drools.scorecards.example" ).getRules().size() > 0 );
        kSession.fireAllRules();
        assertTrue( kSession.getObjects().size() > 0 );


        System.out.println( "************************ REMOVING resource 1 ");

        ChangeSetHelperImpl csRem = new ChangeSetHelperImpl();
        csRem.addRemovedResource( res );
        kAgent.applyChangeSet( csRem .getChangeSet() );

        kSession.fireAllRules();

        assertEquals( 0, kBase.getKnowledgePackage( "org.drools.scorecards.example" ).getRules().size() );

        System.err.println( reportWMObjects( kSession ) );
        assertEquals( 0, kSession.getObjects().size() );

        kSession.dispose();
        kAgent.dispose();
    }





    private StatefulKnowledgeSession loadModel( String source ) {
        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        knowledgeBuilder.add( ResourceFactory.newClassPathResource( "org/drools/informer/informer-changeset.xml" ), ResourceType.CHANGE_SET );

        knowledgeBuilder.add( ResourceFactory.newClassPathResource( source ), ResourceType.PMML );
        if ( knowledgeBuilder.hasErrors() ) {
            fail( knowledgeBuilder.getErrors().toString() );
        }

        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();

        knowledgeBase.addKnowledgePackages( knowledgeBuilder.getKnowledgePackages() );
        StatefulKnowledgeSession kSession = knowledgeBase.newStatefulKnowledgeSession();

        kSession.fireAllRules();
        return kSession;
    }

    private KnowledgeAgent initIncrementalKA() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "org/drools/informer/informer-changeset.xml" ), ResourceType.CHANGE_SET );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KnowledgeAgentConfiguration kaConfig = KnowledgeAgentFactory.newKnowledgeAgentConfiguration();
        kaConfig.setProperty( NewInstanceOption.PROPERTY_NAME, "false" );
        kaConfig.setProperty( UseKnowledgeBaseClassloaderOption.PROPERTY_NAME, "true" );
        KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent( "testPmml", kbase, kaConfig );
        return kagent;
    }



}
