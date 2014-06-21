package org.jfugue;

import java.util.HashMap;

/**
 * This class is used to transform a pattern.  Extend this class to create your own
 * PatternTransformer, which
 * listens to parser events and can modify the events that are fired off by the parser.
 * Some sample
 * PatternTransformer subclasses are packaged with JFugue; refer to those to see examples
 * of transformers in action.
 *
 * @see org.jfugue.demo.DurationPatternTransformer
 * @see org.jfugue.demo.IntervalPatternTransformer
 * @see org.jfugue.demo.ReversePatternTransformer
 * @author David Koelle
 * @version 2.0
 */
public abstract class PatternTransformer implements ParserListener
{
    private HashMap variables;

    public PatternTransformer()
    {
        variables = new HashMap();
    }

    /**
     * Returns a list of variables needed by your Transformer.
     *
     * <p>
     * It is very important to keep these strings standardized! This will allow
     * future programs to understand what is required of each Transformer. Therefore,
     * make your Transformer return strings like this. Notice the use of quotes and slashes.
     * </p>
     *
     * <code>'variable name'/type/description/default</code>
     *
     * <p>
     * If you need to return multiple Strings, end each String with a \n, the newline character.
     * </p>
     *
     * <p>
     * Also, be sure to add your strings to the JavaDoc for the getVariables() method as
     * well, so users can learn what your code needs from its documentation.
     * </p>
     */
    public abstract String getVariables();

    /**
     * Indicates what this PatternTransformer does.
     * @return A String giving a quick description of this transformer
     */
    public abstract String getDescription();

    /** Places a value for a variable. */
    public void putVariable(String name, Object value)
    {
        variables.put(name,value);
    }

    /** Returns a variable setting. */
    public Object getVariable(String name)
    {
        return variables.get(name);
    }

    /** Contains the pattern to return at the end of the transformation. */
    protected Pattern returnPattern;

    /** Transforms the pattern, according to the event method that you have
     *  presumably extended.
     */
    public Pattern transform(Pattern p)
    {
        returnPattern = new Pattern();
        Parser parser = new Parser();
        parser.addParserListener(this);
        try {
            parser.parse(p);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return returnPattern;
    }

    /** Extend this method to make your transformer modify the voice. */
    public void voiceEvent(Voice voice)
    {
        returnPattern.addElement(voice);
    }

    /** Extend this method to make your transformer modify the tempo. */
    public void tempoEvent(Tempo tempo)
    {
        returnPattern.addElement(tempo);
    }

    /** Extend this method to make your transformer modify the instrument. */
    public void instrumentEvent(Instrument instrument)
    {
        returnPattern.addElement(instrument);
    }

    /** Extend this method to make your transformer modify the controller messages. */
    public void controllerEvent(Controller controller)
    {
        returnPattern.addElement(controller);
    }

    /** Extend this method to make your transformer modify the note.
     *  Don't forget to also extend sequentialNoteEvent and parallelNoteEvent.
     */
    public void noteEvent(Note note)
    {
        returnPattern.addElement(note);
    }

    /** Extend this method to make your transformer modify the note.
     *  Don't forget to also extend noteEvent and parallelNoteEvent.
     */
    public void sequentialNoteEvent(Note note)
    {
        returnPattern.addElement(note);
    }

    /** Extend this method to make your transformer modify the note.
     *  Don't forget to also extend noteEvent and sequentialNoteEvent.
     */
    public void parallelNoteEvent(Note note)
    {
        returnPattern.addElement(note);
    }
}

