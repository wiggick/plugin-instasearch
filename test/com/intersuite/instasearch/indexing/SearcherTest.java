/*
 * Copyright (c) 2009 Andrejs Jermakovics.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrejs Jermakovics - initial implementation
 */
package com.intersuite.instasearch.indexing;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.lucene.index.IndexWriter;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit test for Searcher
 */
public class SearcherTest 
{
	private static TestSearcher searcher;
	private static StorageIndexer indexer;
	private static int numDocs;

	@BeforeClass
	public static void indexStuff() throws IOException, CoreException
	{
		indexer = new StorageIndexer();
		indexTestFiles();
		searcher = new TestSearcher(indexer.getIndexDir());
	}
	
	@Test
	public void testSimpleText() throws Exception
	{
		List<SearchResultDoc> docs = search("unique");
		assertEquals(1, docs.size());
		
		docs = search("another");
		assertEquals(3, docs.size());
		
		docs = search("another other");
		assertEquals(2, docs.size());
		
		docs = search("another_other");
		assertEquals(4, docs.size());
	}
	
	@Test
	public void testCodeSearches() throws Exception
	{
		assertFileMatches("file5.txt", "HTTP_REFERER", true);
		assertFileMatches("file5.txt", "HTTP_REFER");
		assertFileMatches("file5.txt", "attp refered"); // approximate
		assertFileMatches("file5.txt", "$_POST['HTTP_REFERER']");
		assertFileMatches("file5.txt", "redirect($_POST['HTTP_REFERER']);");
		assertFileMatches("file5.txt", "redirect http referer");
		
		assertFileMatches("file6.txt", "TOPIC_OF_INTEREST", true);
		assertFileMatches("file6.1.txt", "\"TOPIC OF INTEREST\"");
		
		List<SearchResultDoc> docs = search("TOPIC_OF_INTEREST");
		assertEquals(1, docs.size()); // contain topic
		
		docs = search("TOPIC");
		assertEquals(4, docs.size()); // contain topic
		
		assertFileMatches("file7.txt", "driveLetter");
		assertFileMatches("file7.txt", "server.driveLetter");
		assertFileMatches("file7.txt", "server driveLetter");
		assertFileMatches("file7.txt", "server drive Letter");
		assertFileMatches("file7.txt", "getText(server.driveLetter, int a);");
		assertFileMatches("file7.txt", "getString(my.variable, int var2) { }");
		
		assertFileMatches("file1.txt", "file1.txt", "file8.txt");
		assertFileMatches("file2.txt", "file2.txt", "file8.txt");
		assertFileMatches("file4.xml", "file4.xml", "file8.txt");
		
		assertFileMatches("file9.txt", "idl_Ty");
		assertFileMatches("file10.txt", "idlTypeForNonGeneratedPorts");
		assertFileMatches("file10.txt", "idl Type For_Non");
		
		assertFileMatches("file11.txt", "pointer->execMethod");
		assertFileMatches("file11.txt", "test3_mtdDecl");
	}
	
	@Test
	public void testMoreCodeSearches() throws Exception
	{
		assertFileMatches("file6.1.txt", "iNTEReST TOpIc");
		assertFileMatches("file16.txt", "\"class MethodClassifier\"");
		assertFileMatches("file16.txt", "\"class MethodClassifier\"~1");
		assertFileMatches("file16.txt", "class MethodClassifier");
	
		assertFileMatches("file16.txt", "\"class MethodClassifier\" proj:proj1");
		
		assertFileMatches("file15.txt", "\"new MethodClassifier\"");
		assertFilesMatch("class MethodClassifier", false, "file16.txt", "file17.txt");
	}
	
	@Test
	public void testCamelCaseSearch() throws Exception
	{
		assertFileMatches("file12.txt", "mainTopicHandler");
		assertFileMatches("file4.xml", "MyFirstServer");
	}
	
	@Test
	public void testFieldSearches() throws Exception
	{
		assertFileMatches("file8.txt", "name:file8.txt");
		assertFileMatches("file8.txt", "name:file8");
		assertFileMatches("file8.txt", "name:file8*");
		
		assertFileMatches("FileWithoutExtension", "name:FWE");
		assertFileMatches("FileWithoutExtension", "without extension");
		assertFileMatches("FileWithoutExtension", "FileWithoutExtension");
		assertFileMatches("FileWithoutExtension", "FileWithoutExt");
		
		assertFileMatches("file4.xml", "ext:xml");
		assertFileMatches("file4.xml", "handler ext:xml");
		assertFileMatches("file4.xml", "handler MyFirstServer ext:*");
		
		List<SearchResultDoc> docs = search("proj:proj1");
		assertEquals(16, docs.size());
		
		docs = search("proj:\"proj three\"");
		assertEquals(3, docs.size());
	}

	@Test
	public void testHyphenatedWords() throws Exception
	{
		assertFileMatches("file14.txt", "body-css-style", true);
		
		assertFileMatches("file13.txt", "body css style", true);

		assertFilesMatch("body css style", false, "file13.txt", "file14.txt");
	}
	
	@Test
	public void testModifiedDateSearches() throws Exception
	{
		List<SearchResultDoc> found;
		
		found = search("modified:\"1 day\"");
		assertEquals(numDocs, found.size());
		
		found = search("modified:today");
		assertEquals(numDocs, found.size());
	}
	
	private void assertFileMatches(String expectedFile, String searchString, String... otherFiles) throws Exception
	{
		List<SearchResultDoc> docs = search(searchString);
		assertEquals(expectedFile, docs.get(0).getFileName());
		
		assertEquals(true, docs.size() > otherFiles.length);
		
		for(int i = 0; i < otherFiles.length && i+1 < docs.size(); i++)
		{
			assertEquals(otherFiles[i], docs.get(i+1).getFileName());
		}
	}
	
	private void assertFilesMatch(String searchString, boolean doExactSearch, String... expectedFiles) throws Exception
	{
		AtomicBoolean isExact = new AtomicBoolean(doExactSearch);
		List<SearchResultDoc> docs = search(searchString, isExact );
		
		assertEquals(true, docs.size() >= expectedFiles.length);
		
		for(int i = 0; i < expectedFiles.length; i++)
		{
			assertEquals(expectedFiles[i], docs.get(i).getFileName());
		}
		
		assertEquals("Query changed exact flag", doExactSearch, isExact.get());
	}
	
	private void assertFileMatches(String expectedFile, String searchString, boolean exact) throws Exception
	{
		AtomicBoolean isExact = new AtomicBoolean(exact);
		List<SearchResultDoc> docs = search(searchString, isExact );

		assertEquals(expectedFile, docs.get(0).getFileName());
		assertEquals("Exact query comparison failed", exact, isExact.get());
	}
	
	private static void indexTestFiles() throws IOException, CoreException 
	{	
		IndexWriter writer = indexer.createIndexWriter(true);

		indexFile(writer, "/path/FileWithoutExtension", "a file without an extension");
		
		indexFile(writer, "/path/file1.txt", "this is a text file with some unique contents");
		indexFile(writer, "/path/file2.txt", "this is another text file with some other contents");
		indexFile(writer, "/path/file3.txt", "this is yet another text file with some other contents");
		
		indexFile(writer, "/path/file4.xml", "<handler class=\"com.app.command.MyFirstServer\"/>", "proj2");
		indexFile(writer, "/path/file5.txt", "if(true)\n{redirect($_POST['HTTP_REFERER']);\n}", "proj three");
		indexFile(writer, "/path/file6.txt", "this is yet another text file with TOPIC_OF_INTEREST");
		indexFile(writer, "/path/file6.1.txt", "this is some other text file with TOPIC of INTEREST", "proj three");
		indexFile(writer, "/path/file6.2.txt", "this file contains a TOPIC", "proj three");	
		indexFile(writer, "/path/file7.txt", "getText(server.driveLetter, int a); getString(my.variable, int var2) { }");
		
		indexFile(writer, "/path/file8.txt", "/path/file1.txt \"file2.txt\" file3.txt;file4.xml");
		indexFile(writer, "/path/file9.txt", "<handler class=\"id_lTy\" name=\"nm_lTy\"/>");
		indexFile(writer, "/path/file10.txt", "String s = \"idlTypeForNonGeneratedPorts\";");
		indexFile(writer, "/path/file11.txt", "pointer->execMethod(); new Main()->test3_mtdDecl();");
		indexFile(writer, "/path/file12.txt", "public void mainTopicHandler(){ new MyClass(); }");
		
		indexFile(writer, "/path/file13.txt", "body css style body css style body css style");	
		indexFile(writer, "/path/file14.txt", "some file with body-css-style ");
		indexFile(writer, "/path/file15.txt", "Assert.assertEquals(\"Setter\", expected (new MethodClassifier) is");
		indexFile(writer, "/path/file16.txt", "class MethodClassifier() { Integer a = new Integer(1); String s = new String(); } ");
		indexFile(writer, "/path/file17.txt", "MethodClassifier(); main() { class Different { static class B; } }");
		
		numDocs = writer.numDocs();
		writer.close();
	}

	private static void indexFile(IndexWriter writer, String path, String contents) throws CoreException, IOException 
	{
		indexFile(writer, path, contents, "proj1");
	}
	
	private static void indexFile(IndexWriter writer, String path, String contents, String proj) throws CoreException, IOException 
	{
		IStorage file1 = new TestStorage(path, contents);
		long time = System.currentTimeMillis() - 1000;
		indexer.indexStorage(writer, file1, proj, time, null);
	}
	
	private List<SearchResultDoc> search(String searchString) throws Exception 
	{
		return search(searchString, new AtomicBoolean(true) );
	}
	
	private List<SearchResultDoc> search(String searchString, AtomicBoolean isExact) throws Exception 
	{
		SearchQuery searchQuery = new SearchQuery(searchString, SearchQuery.UNLIMITED_RESULTS);
		searchQuery.setExact( isExact.get() );
		SearchResult res = searcher.search(searchQuery);
		
		isExact.set( searchQuery.isExact());
		
		if( res == null )
			return Collections.emptyList();

		return res.getResultDocs();
	}
}
