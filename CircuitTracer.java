import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.io.FileNotFoundException;

/**
 * Search for shortest paths between start and end points on a circuit board
 * as read from an input file using either a stack or queue as the underlying
 * search state storage structure and displaying output to the console or to
 * a GUI according to options specified via command-line arguments.
 * 
 * @author mvail, santiagovargas
 * @version CS221 Summer 2025
 */
public class CircuitTracer
{

	/**
	 * Launch the program.
	 * 
	 * @param args three required arguments:
	 *             first arg: -s for stack or -q for queue
	 *             second arg: -c for console output or -g for GUI output
	 *             third arg: input file name
	 */
	public static void main(String[] args)
	{
		new CircuitTracer(args);
	}

	/*
     * Print instructions for running CircuitTracer from the command line.
	 * Print clear usage instructions when command-line arguments are invalid.
     */
	private void printUsage()
	{
		System.out.println("Usage: java CircuitTracer {-s|-q} {-c|-g} <input_file>");
		System.out.println("  -s : use a stack (depth-first search)");
		System.out.println("  -q : use a queue (breadth-first search)");
		System.out.println("  -c : display solutions in the console");
		System.out.println("  -g : display solutions in a GUI");
	}

	/**
	 * Set up the CircuitBoard and all other components based on command
	 * line arguments.
	 * 
	 * @param args command line arguments passed through from main()
	 */

	public CircuitTracer(String[] args) 
	{
		if (args.length != 3)
		{
			printUsage();
			return;
		}

		// Process and validate arguments
		boolean useStack = false;
		boolean useQueue = false;
		boolean consoleOutput = false;
		boolean guiOutput = false;


        // Check if use wants to use Stack or Queue and then update the variables accoringly. 
		if ("-s".equals(args[0]))
		{
			useStack = true;
		} 
		else if ("-q".equals(args[0]))
		{
			useQueue = true;
		} 
		else
		{
			System.out.println("Invalid storage option: " + args[0]);
			printUsage();
			return;
		}

        // Check if use wants to use console or GUI.
		if ("-c".equals(args[1]))
		{
			consoleOutput = true;
		}
		else if ("-g".equals(args[1]))
		{
			guiOutput = true;
		}
		else
		{
			System.out.println("Invalid output option: " + args[1]);
			printUsage();
			return;
		}

		String fileName = args[2];
		CircuitBoard board;

        // Makes a board Object which checks if the format is correcet. If not throw exception.
		try
        {
			board = new CircuitBoard(fileName);
		}
		catch (InvalidFileFormatException e)
		{
			System.out.println("InvalidFileFormatException: " + e.toString());
			return;
		}
		catch (FileNotFoundException e)
		{
			System.out.println("FileNotFoundException: " + e.getMessage());
			return;
		}

		Storage<TraceState> stateStore = null;

        /*
         * It hurt my soul to not use ternary operators here.
         */
        if (useStack)
        {
		        stateStore = Storage.getStackInstance();
        }
        else if (useQueue)
        {
		        stateStore = Storage.getQueueInstance();
        };

		List<TraceState> bestPaths = new ArrayList<>();

		Point start = board.getStartingPoint();
		int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };

		for (int[] d : directions)
		{
			int r = start.x + d[0];
			int c = start.y + d[1];

			if (r >= 0 && r < board.numRows() && c >= 0 && c < board.numCols())
			{
				if (board.isOpen(r, c))
				{
					try {
						stateStore.store(new TraceState(board, r, c));
					}
					catch (Exception e)
					{
						/*
						 * Trace couldn't be added at this position. This can be ignored.
                         * Its probably best to not print this due to the way the tests check
                         * for console output so it might cause tests to fail if we println
                         * something it does not except. It is just good to keep in mind that 
                         * this is something that can happen.
                         */
					}
				}
			}
		}

		while (!stateStore.isEmpty())
		{
			TraceState current = stateStore.retrieve();

			if (current.isSolution()) 
			{
				if (bestPaths.isEmpty())
				{
					bestPaths.add(current);
				}
				else
				{
					int bestLen = bestPaths.get(0).pathLength();
					int curLen = current.pathLength();

					if (curLen < bestLen)
					{
						bestPaths.clear();
						bestPaths.add(current);
					}
					else if (curLen == bestLen)
					{
						bestPaths.add(current);
					}
				}
			}
			else
			{
				for (int[] d : directions)
				{
					int r = current.getRow() + d[0];
					int c = current.getCol() + d[1];

					if (r >= 0 && r < board.numRows() && c >= 0 && c < board.numCols())
					{
						if (current.isOpen(r, c))
						{
							try
							{
								stateStore.store(new TraceState(current, r, c));
							}
							catch (Exception e)
							{
                                /*
								 * Trace couldn't be added at this position. This can be ignored.
                                 * Its probably best to not print this due to the way the tests check
                                 * for console output so it might cause tests to fail if we println
                                 * something it does not except. It is just good to keep in mind that 
                                 * this is something that can happen.
                                 */
							}
						}
					}
				}
			}
		}

        /*
         * If -c is found in arguments then just print to console else start the GUI with the board 
         * and all the solutions.
         */
		if (consoleOutput)
		{
			for (TraceState solution : bestPaths)
			{
				System.out.println(solution);
			}
		}
		else if (guiOutput) 
		{
			new CircuitTracerGUI(board, bestPaths);
		}
	}
}
