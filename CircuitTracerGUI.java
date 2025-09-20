import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Graphical Viewer for Cuircuit trace solutions. This is just a basic Swing application 
 * that adds labels, panels and menu options according to the extra credit requirements.
 *
 * @author santiagovargas
 * @version CS221 Summer 2025
 */
public class CircuitTracerGUI extends JFrame
{

	private final JLabel[][] gridLabels;
	private final CircuitBoard baseBoard;

    /**
     * Builds and displays the GUI immediately.
     *
     * @param original   the solved CircuitBoard used to display
     * @param bestPaths  all solutions with smallest length of the CircuitBoard 
     *                   object passed in.
     */
	public CircuitTracerGUI(CircuitBoard original, List<TraceState> bestPaths)
	{
		super("Circuit Tracer Search");

		this.baseBoard = original;

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		// Menu Bar variables and Logic
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenu helpMenu = new JMenu("Help");
		JMenuItem aboutItem = new JMenuItem("About...");
		JMenuItem quitItem = new JMenuItem("Quit");

		aboutItem.addActionListener(e ->
		{
    			JOptionPane.showMessageDialog(
				this,
				"Circuit Tracer Search\n\nWritten by Santiago Vargas (santiagovargas@u.boisestate.edu)",
				"About",
				JOptionPane.INFORMATION_MESSAGE
			);
		});
 
		quitItem.addActionListener(e ->
		{
    			dispose(); // close the window
    			System.exit(0); // end the program
		});
 
		fileMenu.add(quitItem);
		helpMenu.add(aboutItem);
		menuBar.add(fileMenu);
		menuBar.add(helpMenu);
		setJMenuBar(menuBar);

		// Create board panel
		JPanel boardPanel = new JPanel(new GridLayout(original.numRows(), original.numCols()));
		gridLabels = new JLabel[original.numRows()][original.numCols()];

		for (int r = 0; r < original.numRows(); r++)
		{
			for (int c = 0; c < original.numCols(); c++)
			{
				JLabel label = new JLabel(Character.toString(original.charAt(r, c)), SwingConstants.CENTER);
				label.setBorder(BorderFactory.createLineBorder(Color.GRAY));
				label.setOpaque(true);
				label.setBackground(Color.WHITE);
				gridLabels[r][c] = label;
				boardPanel.add(label);
			}
		}

		// Create solution list
		DefaultListModel<String> model = new DefaultListModel<>();
		for (int i = 0; i < bestPaths.size(); i++)
		{
			model.addElement("Path #" + (i + 1) + " (Length: " + bestPaths.get(i).pathLength() + ")");
		}

		JList<String> pathList = new JList<>(model);
		pathList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPane = new JScrollPane(pathList);

		pathList.addListSelectionListener(e ->
		{
			if (!e.getValueIsAdjusting())
			{
				int index = pathList.getSelectedIndex();
				if (index >= 0)
				{
					displayPath(bestPaths.get(index));
				}
			}
		});

		// Add to frame
		add(boardPanel, BorderLayout.CENTER);
		add(scrollPane, BorderLayout.EAST);

		setLocationRelativeTo(null);
		setPreferredSize(new Dimension(600,400));
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	/** Display a solution by recoloring the grid. */
	private void displayPath(TraceState state)
	{
		// Reset to base board characters and colors
		for (int r = 0; r < baseBoard.numRows(); r++)
		{
			for (int c = 0; c < baseBoard.numCols(); c++)
			{
				char ch = baseBoard.charAt(r, c);
				gridLabels[r][c].setText(Character.toString(ch));
				gridLabels[r][c].setBackground(Color.WHITE);
			}
		}

		// Show the trace ('T' path) in yellow
		for (Point p : state.getPath())
		{
			gridLabels[p.x][p.y].setText("T");
			gridLabels[p.x][p.y].setBackground(Color.GRAY);
		}
	}
}
