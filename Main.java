import javax.swing.*;
import java.awt.event.*;

public class Main extends JFrame implements ActionListener {
    // Components
    private JTextField numSubjectsField;
    private JButton enterButton;

    public Main () {
        setTitle("Grade Calculator");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(null);

        // Number of Subjects field
        JLabel numSubjectsLabel = new JLabel("How Many Subjects: ");
        numSubjectsLabel.setBounds(20, 20, 150, 20);
        add(numSubjectsLabel);

        numSubjectsField = new JTextField();
        numSubjectsField.setBounds(180, 20, 150, 20);
        add(numSubjectsField);

        // Enter Button
        enterButton = new JButton("Enter");
        enterButton.setBounds(120, 60, 150, 30);
        enterButton.addActionListener(this);
        add(enterButton);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == enterButton) {
            int numSubjects = Integer.parseInt(numSubjectsField.getText());

            // Hide current frame
            setVisible(false);
            dispose(); 

            // Open new frame for entering subject names
            new SubjectNamesFrame(numSubjects);
        }
    }

    public static void main(String[] args) {
        new Main();
    }
}

class SubjectNamesFrame extends JFrame implements ActionListener {
    private int numSubjects;
    private JTextField[] subjectNameFields;
    private JButton continueButton;

    public SubjectNamesFrame(int numSubjects) {
        this.numSubjects = numSubjects;

        setTitle("Enter Subject Names");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(null);

        // Creating input fields for subject names
        int y = 20;
        subjectNameFields = new JTextField[numSubjects];
        for (int i = 0; i < numSubjects; i++) {
            JLabel subjectNameLabel = new JLabel("Name of Subject " + (i + 1) + ": ");
            subjectNameLabel.setBounds(20, y, 150, 20);
            add(subjectNameLabel);

            subjectNameFields[i] = new JTextField();
            subjectNameFields[i].setBounds(180, y, 150, 20);
            add(subjectNameFields[i]);

            y += 40;
        }

        // Continue Button
        continueButton = new JButton("Continue");
        continueButton.setBounds(120, y, 150, 30);
        continueButton.addActionListener(this);
        add(continueButton);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == continueButton) {
            // Hide current frame
            setVisible(false);
            dispose(); // Release resources

            // Open new frame for entering grades
            new GradesFrame(numSubjects, subjectNameFields);
        }
    }
}

class GradesFrame extends JFrame implements ActionListener {
    private int numSubjects;
    private JTextField[][] subjectGradeFields;
    private JButton enterButton;

    public GradesFrame(int numSubjects, JTextField[] subjectNameFields) {
        this.numSubjects = numSubjects;

        setTitle("Enter Grades");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);


        // Creating input fields for grades
        int y = 20;
        subjectGradeFields = new JTextField[numSubjects][3];
        for (int i = 0; i < numSubjects; i++) {
            JLabel subjectNameLabel = new JLabel("Subject: " + subjectNameFields[i].getText());
            subjectNameLabel.setBounds(20, y, 200, 20);
            add(subjectNameLabel);

            for (int j = 0; j < 3; j++) {
                JLabel gradeLabel = new JLabel("Grade for P" + (j + 1) + ": ");
                gradeLabel.setBounds(20 + (j * 150), y + 30, 100, 20);
                add(gradeLabel);

                subjectGradeFields[i][j] = new JTextField();
                subjectGradeFields[i][j].setBounds(120 + (j * 150), y + 30, 50, 20);
                add(subjectGradeFields[i][j]);
            }

            y += 60;
        }

        // Enter Button
        enterButton = new JButton("Enter");
        enterButton.setBounds(120, y, 150, 30);
        enterButton.addActionListener(this);
        add(enterButton);

        setVisible(true);
    }

    @Override
public void actionPerformed(ActionEvent e) {
    if (e.getSource() == enterButton) {
        // Check for empty grade fields
        boolean hasEmptyGrades = false;
        for (int i = 0; i < numSubjects; i++) {
            for (int j = 0; j < 3; j++) {
                if (subjectGradeFields[i][j].getText().isEmpty()) {
                    hasEmptyGrades = true;
                    break;
                }
            }
            if (hasEmptyGrades) {
                break;
            }
        }

        // If there are empty grades, prompt the user
        if (hasEmptyGrades) {
            int option = JOptionPane.showConfirmDialog(null, "Some grade fields is empty. Are you sure these are all the grades you have?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.NO_OPTION) {
                return; // Return without calculating grades
            }
        }

        // Perform grade conversion and calculation
        double p1Total = 0;
        double p2Total = 0;
        double p3Total = 0;
        int totalGradesEntered = 0;

        for (int i = 0; i < numSubjects; i++) {
            for (int j = 0; j < 3; j++) {
                if (!subjectGradeFields[i][j].getText().isEmpty()) {
                    int traditionalGrade = Integer.parseInt(subjectGradeFields[i][j].getText());
                    double convertedGrade = convertGrade(traditionalGrade);

                    if (j == 0) {
                        p1Total += convertedGrade;
                    } else if (j == 1) {
                        p2Total += convertedGrade;
                    } else if (j == 2) {
                        p3Total += convertedGrade;
                    }

                    totalGradesEntered++;
                }
            }
        }

        // Calculate averages
        double p1Average = p1Total / numSubjects;
        double p2Average = totalGradesEntered >= numSubjects ? p2Total / numSubjects : Double.NaN;
        double p3Average = totalGradesEntered >= numSubjects * 2 ? p3Total / numSubjects : Double.NaN;

        // Calculate GWA
        double gwa = (p1Average + p2Average + p3Average) / 3;

        // Check if there are empty grades and recommend a grade if needed
        if (hasEmptyGrades) {
            double remainingAverageNeeded = 1.50 * 3 - gwa;
            double suggestedGrade = remainingAverageNeeded * numSubjects - p2Total;

            JOptionPane.showMessageDialog(null, "Average converted grade for P1: " + p1Average + "\n" +
                "Average converted grade for P2: " + (Double.isNaN(p2Average) ? "N/A" : p2Average) + "\n" +
                "Average converted grade for P3: " + (Double.isNaN(p3Average) ? "N/A" : p3Average) + "\n" +
                "GWA: " + gwa + "\n" +
                "To pass the semester, you need to achieve an average grade of at least " + String.format("%.2f", remainingAverageNeeded) + " in the remaining assessments (P2 and/or P3).\n" +
                "Suggested Grade: " + String.format("%.2f", suggestedGrade));
        } else {
            // Output results
            JOptionPane.showMessageDialog(null, "Average converted grade for P1: " + p1Average + "\n" +
                    "Average converted grade for P2: " + (Double.isNaN(p2Average) ? "N/A" : p2Average) + "\n" +
                    "Average converted grade for P3: " + (Double.isNaN(p3Average) ? "N/A" : p3Average) + "\n" +
                    "GWA: " + gwa + "\n" +
                    (gwa <= 1.5 ? "Congratulations! You passed the Semester." : "You failed the Semester."));
        }
    }
}

    // Grade conversion method
    public static double convertGrade(int traditionalGrade) {
        if (traditionalGrade >= 94.8 && traditionalGrade <= 100) {
            return 1;
        } else if (traditionalGrade >= 89.2 && traditionalGrade < 94.7) {
            return 1.25;
        } else if (traditionalGrade >= 83.6 && traditionalGrade < 89.1) {
            return 1.50;
        } else if (traditionalGrade >= 78 && traditionalGrade < 83.5) {
            return 1.75;
        } else if (traditionalGrade >= 72.4 && traditionalGrade < 77.9) {
            return 2;
        } else if (traditionalGrade >= 66.8 && traditionalGrade < 72.3) {
            return 2.25;
        } else if (traditionalGrade >= 61.2 && traditionalGrade < 66.7) {
            return 2.50;
        } else if (traditionalGrade >= 55.6 && traditionalGrade < 61.1) {
            return 2.75;
        } else if (traditionalGrade >= 50.0 && traditionalGrade < 55.5) {
            return 3;
        } else if (traditionalGrade >= 0 && traditionalGrade < 49.9) {
            return 5;
        } else {
            return 0;
        }
    }

    // Sample P2 grade calculation method
    public static double calculateSampleP2Grade(double p1Average) {
        return (1.5 * 3) - p1Average;
    }

    // Sample P3 grade calculation method
    public static double calculateSampleP3Grade(double p1Average, double p2Average) {
        return (1.5 * 3) - p1Average - p2Average;
    }
}