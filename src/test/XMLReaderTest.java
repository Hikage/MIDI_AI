package test;

import static org.junit.Assert.*;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import sax.XMLReader;
import thriveTones.Chord;
import thriveTones.ChordDictionary;
import thriveTones.SongSegment.SongPart;

/**
 * "ThriveTones" Song Generator
 * Copyright © 2014 Brianna Shade
 * bshade@pdx.edu
 *
 * XMLReaderTest.java
 * Tests the XMLReader class
 */

public class XMLReaderTest {
	private static NodeList rows;
	private static XMLReader reader;
	private static String file = "Hooktheory-Data.xml";
	private static ChordDictionary chord_dictionary;

	/**
	 * Reads in the data file in preparation for tests
	 */
	@BeforeClass
	public static void XMLReaderInit() {
		reader = new XMLReader();
		
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder builder = factory.newDocumentBuilder();
		    	    
		    FileReader freader = new FileReader(file);
		    Document document = builder.parse(new InputSource(freader));
		    
		    NodeList results = document.getElementsByTagName("resultset");
		    rows = results.item(0).getChildNodes();
		}
		catch(Exception e){
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * Tests the initial read-in
	 */
	@Test
	public void testReadIn(){
		try {
			reader.readIn(file);
		}
		catch (Exception e){
			e.printStackTrace();
			fail(e.getMessage());
		}

		assertEquals(17, rows.item(1).getChildNodes().getLength());
		assertEquals("Jimmy Eat World", rows.item(1).getChildNodes().item(1).getTextContent().trim());
	}

	/**
	 * Ensures the dictionary was built as expected
	 */
	@Test
	public void testDictionaryBuild(){
		HashMap<SongPart, ChordDictionary> parts_dictionary = reader.getPartsDictionary();
		if(parts_dictionary == null || parts_dictionary.isEmpty()){
			try {
				reader.readIn(file);
			}
			catch (Exception e){
				e.printStackTrace();
				fail(e.getMessage());
			}
		}

		assertTrue(parts_dictionary.containsKey(SongPart.chorus));
		assertFalse(parts_dictionary.get(SongPart.chorus).isEmpty());
		assertTrue(parts_dictionary.containsKey(SongPart.verse));
		assertFalse(parts_dictionary.get(SongPart.verse).isEmpty());
		assertTrue(parts_dictionary.containsKey(SongPart.bridge));
		assertFalse(parts_dictionary.get(SongPart.bridge).isEmpty());
		assertTrue(parts_dictionary.containsKey(SongPart.solo));
		assertFalse(parts_dictionary.get(SongPart.solo).isEmpty());

		chord_dictionary = parts_dictionary.get(SongPart.chorus);

		// Test empty chord pull
		int[] roots = new int[8];
		for(int i = 0; i < 100; i++){
			Chord next = chord_dictionary.getANextChord(null, false);
			roots[next.getRoot()]++;
		}
		for(int count : roots)
			System.out.println("Root count: " + count + "/" + chord_dictionary.get(new LinkedList<Chord>()).size());
		System.out.println();
		assertTrue(roots[1] >= 15);
		assertTrue(roots[1] >= roots[5]-10);
		assertTrue(roots[5] >= roots[2]);

		// Test single-chord lookups
		for(int i = 0; i < 10; i++){
			Chord next;
			LinkedList<Chord> sequence = new LinkedList<Chord>();
			next = chord_dictionary.getANextChord(null, false);
			sequence.add(next);
			ArrayList<Chord> available_chords = chord_dictionary.get(sequence);

			boolean same = true;
			for(int j = 1; j < available_chords.size(); j++){
				same = (available_chords.get(j).equals(available_chords.get(j-1)));
				if(!same) break;
			}
		}
	}

	/**
	 * Tests nodeValueByAttName()
	 */
	@Test
	public void testNodeValueByAttName(){
		NodeList fields = rows.item(1).getChildNodes();
		assertEquals("Jimmy Eat World", reader.nodeValueByAttName(fields, "artist"));
		assertEquals("The Middle", reader.nodeValueByAttName(fields, "song"));
		assertEquals("Intro and Verse", reader.nodeValueByAttName(fields, "section"));
		assertEquals(",1-8,5-8,4-8,1-8,1-8,5-8,4-8,1-8,", reader.nodeValueByAttName(fields, "SIF"));
		assertEquals("4", reader.nodeValueByAttName(fields, "beatsInMeasure"));
		assertEquals("Eb", reader.nodeValueByAttName(fields, "songKey"));
		assertEquals("146", reader.nodeValueByAttName(fields, "bpm"));
		assertEquals("1", reader.nodeValueByAttName(fields, "mode"));
		assertEquals("", reader.nodeValueByAttName(fields, "badFieldName"));
	}

	/**
	 * Tests XMLKeytoKey()
	 */
	@Test
	public void testValidXMLKeytoKey(){
		assertEquals("A", reader.XMLKeytoKey("A"));

		assertEquals("Bb", reader.XMLKeytoKey("Bb"));
		assertEquals("Cb", reader.XMLKeytoKey("CB"));
		assertEquals("Db", reader.XMLKeytoKey("Df"));
		assertEquals("Eb", reader.XMLKeytoKey("EF"));

		assertEquals("F#", reader.XMLKeytoKey("F#"));
		assertEquals("G#", reader.XMLKeytoKey("Gs"));
		assertEquals("A#", reader.XMLKeytoKey("AS"));
		assertEquals("B#", reader.XMLKeytoKey("bS"));
	}

	/**
	 * Tests invalid XML keys
	 */
	@Test
	public void testInvalidXMLKeytoKey(){
		assertNull(reader.XMLKeytoKey("H"));
		assertNull(reader.XMLKeytoKey("cy"));
		assertNull(reader.XMLKeytoKey(""));
		assertNull(reader.XMLKeytoKey("14"));
		assertNull(reader.XMLKeytoKey("A*"));
		assertNull(reader.XMLKeytoKey("ABC"));
		assertNull(reader.XMLKeytoKey("sus"));
	}

	/**
	 * Tests extractKey()
	 */
	@Test
	public void testExtractKey(){
		NodeList fields = rows.item(1).getChildNodes();
		try{
			assertEquals("Eb", reader.extractKey(fields));
		}
		catch(Exception e){
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * Tests invalid key to extract
	 * @throws Exception : on invalid key provided
	 */
	@Test (expected = Exception.class)
	public void testNullExtractKey() throws Exception{
		reader.extractKey(null);
	}

	/**
	 * Tests partToEnum()
	 */
    @Test
    public void testPartToEnum(){
        assertEquals(SongPart.bridge, reader.partToEnum("Bridge"));
        assertEquals(SongPart.chorus, reader.partToEnum("Chorus Lead-Out"));
        assertEquals(SongPart.chorus, reader.partToEnum("Chorus"));
        assertEquals(SongPart.solo, reader.partToEnum("Instrumental"));
        assertEquals(SongPart.introverse, reader.partToEnum("Intro and Verse"));
        assertEquals(SongPart.intro, reader.partToEnum("Intro"));
        assertEquals(SongPart.outro, reader.partToEnum("Outro 1"));
        assertEquals(SongPart.outro, reader.partToEnum("Outro 2"));
        assertEquals(SongPart.outro, reader.partToEnum("Outro"));
        assertEquals(SongPart.prechoruschorus, reader.partToEnum("Pre-Chorus and Chorus"));
        assertEquals(SongPart.prechorus, reader.partToEnum("Pre-Chorus"));
        assertEquals(SongPart.outro, reader.partToEnum("Pre-Outro"));
        assertEquals(SongPart.solo, reader.partToEnum("Solo 1"));
        assertEquals(SongPart.solo, reader.partToEnum("Solo 2"));
        assertEquals(SongPart.solo, reader.partToEnum("Solo 3"));
        assertEquals(SongPart.solo, reader.partToEnum("Solo"));
        assertEquals(SongPart.verseprechorus, reader.partToEnum("Verse and Pre-Chorus"));
        assertEquals(SongPart.verse, reader.partToEnum("Verse"));
    }

	/**
	 * Tests SIFtoChords()
	 */
	@Test
	public void testSIFtoChords(){
		NodeList fields = rows.item(1).getChildNodes();
		try{
			assertEquals("15maj/2.0 55maj/2.0 45maj/2.0 15maj/2.0 15maj/2.0 55maj/2.0 45maj/2.0 15maj/2.0",
					reader.SIFtoChords(fields).toString());
		}
		catch(Exception e){
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * Tests null SIFtoChords()
	 * @throws Exception : on a null SIF
	 */
	@Test (expected = Exception.class)
	public void testNullSIFtoChords() throws Exception{
		reader.SIFtoChords(null);
	}
}
