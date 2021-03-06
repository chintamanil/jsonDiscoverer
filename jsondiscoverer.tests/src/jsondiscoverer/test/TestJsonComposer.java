/*******************************************************************************
 * Copyright (c) 2008, 2015
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Javier Canovas (me@jlcanovas.es) 
 *******************************************************************************/



package jsondiscoverer.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.junit.Test;

import jsondiscoverer.JsonComposer;
import jsondiscoverer.JsonSimpleDiscoverer;
import jsondiscoverer.JsonSource;
import jsondiscoverer.JsonSourceSet;


/**
 * Test case for {@link JsonComposer}
 * 
 * @author Javier Canovas (me@jlcanovas.es)
 *
 */
public class TestJsonComposer {

	/**
	 * Test case for {@link JsonComposer#compose()}
	 * 
	 * @throws FileNotFoundException Thrown if the file is not found 
	 * @throws IOException Something went wrong with IO
	 */
	@Test
	public void testCompose() throws FileNotFoundException, IOException {

		Properties api1Info = new Properties();
		api1Info.load(new FileReader("./testData/composer/api1/source1/info.properties"));
		String shortname1 = api1Info.getProperty("shortname");

		JsonSource api1Source1 = new JsonSource(shortname1);
		Properties api1Source1Properties = new Properties();
		api1Source1Properties.load(new FileReader("./testData/composer/api1/source1/json1.properties"));
		api1Source1.addJsonData(
				new StringReader(api1Source1Properties.getProperty("input")), 
				new FileReader(new File("./testData/composer/api1/source1/json1.json")));
		
		JsonSimpleDiscoverer api1Discoverer = new JsonSimpleDiscoverer();
		EPackage ePackageApi1 = api1Discoverer.discover(api1Source1);
		assertNotNull(ePackageApi1);
		
		JsonSourceSet api1SourceSet = new JsonSourceSet(shortname1);
		api1SourceSet.addJsonSource(api1Source1);

		Properties api2Info = new Properties();
		api1Info.load(new FileReader("./testData/composer/api2/source1/info.properties"));
		String shortname2 = api1Info.getProperty("shortname");

		JsonSource api2Source1 = new JsonSource(shortname2);
		Properties api2Source1Properties = new Properties();
		api2Source1Properties.load(new FileReader("./testData/composer/api2/source1/json1.properties"));
		api2Source1.addJsonData(
				new StringReader(api2Source1Properties.getProperty("input")), 
				new FileReader(new File("./testData/composer/api2/source1/json1.json")));
		
		JsonSimpleDiscoverer api2Discoverer = new JsonSimpleDiscoverer();
		EPackage ePackageApi2 = api2Discoverer.discover(api2Source1);
		assertNotNull(ePackageApi2);

		JsonSourceSet api2SourceSet = new JsonSourceSet(shortname2);
		api2SourceSet.addJsonSource(api2Source1);
		
		List<JsonSourceSet> sourceSetList = new ArrayList<JsonSourceSet>();
		sourceSetList.add(api1SourceSet);
		sourceSetList.add(api2SourceSet);
		
		JsonComposer composer = new JsonComposer(sourceSetList);
		EPackage composed = composer.compose();
		assertNotNull(composed);
		
		EClass stopPositionInput = (EClass) composed.getEClassifier("StopPositionInput");
		assertNotNull(stopPositionInput);
		
		assertNotNull(stopPositionInput.getEStructuralFeature("mapping"));
		assertEquals(0, stopPositionInput.getEStructuralFeature("mapping").getLowerBound());
		assertEquals(1, stopPositionInput.getEStructuralFeature("mapping").getUpperBound());
		assertEquals("Location", stopPositionInput.getEStructuralFeature("mapping").getEType().getName());
		

		EClass location = (EClass) composed.getEClassifier("Location");
		assertNotNull(location);
		
		assertNotNull(location.getEStructuralFeature("mapping"));
		assertEquals(0, location.getEStructuralFeature("mapping").getLowerBound());
		assertEquals(1, location.getEStructuralFeature("mapping").getUpperBound());
		assertEquals("StopPositionInput", location.getEStructuralFeature("mapping").getEType().getName());
	}

}
