import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.io.File;
import java.io.IOException;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.Sheet;
import jxl.read.biff.BiffException;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class Aetg {
    public static String[][] values;
    public static HashMap<Integer, Integer> l = new HashMap<Integer, Integer>();
    public static List<int[]> result = new ArrayList<>();
    public static String TITLE;
    public static List<Integer> nums = new ArrayList<>();
    public static int T;
    public static int K;
    public static int M = 50;
    public static long time;
    public static Random r;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        myFrame frame = new myFrame();

    }

    public static void aetg(int k, int[] v, int t) {
        aetgInit(k, v, t);
        int[] temp = new int[k];
        int count = 0;
        while (true) {
            temp = aetgCandidate();
            System.out.println(count++);
            if (temp == null)
                break;
            if (temp[0] == -1) {
                continue;
            }
            result.add(temp);
        }
        aetgDisplay();
    }
    
    //output the results
    private static void aetgDisplay() {
        File xlsFile = new File(T + "-way.xls");
        try {
            WritableWorkbook workbook = Workbook.createWorkbook(xlsFile);
            WritableSheet sheet = workbook.createSheet("sheet1", 0);
            for (int i = 0; i < K; i++) {
                sheet.addCell(new Label(i, 0, values[i][0]));
            }
            int col = 0, row = 0;
            for (int[] temp : result) {
                row++;
                if (temp != null) {
                    col = 0;
                    for (int t : temp) {
                        sheet.addCell(new Label(col, row, values[col][t + 1]));
                        col++;
                    }
                    sheet.addCell(new Label(col, row, nums.get(row - 1).toString()));
                }
            }
            workbook.write();
            workbook.close();
        } catch (Exception e) {
        }
    }
    
    //create a candidate
    private static int[] aetgCandidate() {
        int[] firstIndex = findFirstf();
        if (firstIndex == null)
            return null;
        int[] candidate = new int[K], temp;
        int max = 0;
        for (int i = 0; i < M; i++) {
            temp = aetgCreate(firstIndex);
            if (countNewUncovered(temp) > max) {
                max = countNewUncovered(temp);
                candidate = temp.clone();
            }
        }
        if (max != 0)
            nums.add(max);
        else
            candidate[0] = -1;
        return candidate;
    }
    
    //initialization
    private static void aetgInit(int k, int[] v, int t) {
    	time = System.currentTimeMillis();
        r = new Random(time);
    	for (int i = 0; i < k; i++) {
            l.put(i, v[i]);
        }
        T = t;
        K = k;
        int[] first = new int[v.length];
        
        nums = new ArrayList<>();
        nums.add(fac(K)/(fac(T)*fac(K-T)));
        result= new ArrayList<>();
        result.add(first);
    }
    
    //create a record
    private static int[] aetgCreate(int[] index) {
        Integer[] order = new Integer[K];
        int[] temp = new int[K];
        int cur = 1;
        for (int i = 0; i < order.length; i++) {
            order[i] = i;
        }
        
        Arrays.sort(order, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {

                if (o1 == index[0])
                    return -1;
                if (o2 == index[0])
                    return 1;
                return r.nextInt(1000) % 2 == 0 ? 1 : -1;
            }
        });
        if (index.length == 4) {
            for (int i = 0; i < K; i++) {
                if (order[i] == index[2] && i != 1) {
                    int tt = order[1];
                    order[1] = order[i];
                    order[i] = tt;
                }
            }
            cur++;
            temp[order[1]] = index[3];
        }
        temp[order[0]] = index[1];
        
        for (int i = cur; i < K; i++) {
            temp[order[i]] = findNextf(i, temp, order);
        }

        return temp;
    }
    
    //calculate the number of new uncoverd arrays
    private static int countNewUncovered(int[] temp) {
        if (T == 2)
            return countNewUncovered2(temp);
        return countNewUncovered3(temp);
    }
    
    //calculate the number of new uncoverd arrays--2way
    private static int countNewUncovered2(int[] temp) {
        int sum = 0;
        for (int i = 0; i < K - 1; i++) {
            for (int j = i + 1; j < K; j++) {
                if (!ifContain(i, temp[i], j, temp[j]))
                    sum++;
            }
        }
        return sum;
    }

    //calculate the number of new uncoverd arrays--3way
    private static int countNewUncovered3(int[] temp) {
        int sum = 0;
        for (int i = 0; i < K - 2; i++) {
            for (int j = i + 1; j < K - 1; j++) {
                for (int t = j + 1; t < K; t++) {
                    if (!ifContain(i, temp[i], j, temp[j], t, temp[t]))
                        sum++;
                }
            }
        }
        return sum;
    }
    
    //return the value should be selected of the next variable
    private static int findNextf(int cur, int[] temp, Integer[] order) {
        int[] index = new int[2];
        int max = 0, maxIndex = 0, t = 0;
        index[0] = order[cur];
        for (int i = 0; i < l.get(order[cur]); i++) {
            index[1] = i;
            t = countExistingUncovered(cur, index, temp, order);
            if (t > max) {
                max = t;
                maxIndex = i;
            }
        }
        return maxIndex;
    }
    
    //calculate the number of uncoverd arrays in the results now
    private static int countExistingUncovered(int cur, int[] index, int[] temp, Integer[] order) {
        if (T == 2)
            return countExistingUncovered2(cur, index, temp, order);
        return countExistingUncovered3(cur, index, temp, order);
    }

    //calculate the number of uncoverd arrays in the results now--2way
    private static int countExistingUncovered2(int cur, int[] index, int[] temp, Integer[] order) {
        int sum = 0;
        for (int i = 0; i < cur; i++) {
            if (!ifContain(index[0], index[1], order[i], temp[order[i]]))
                sum++;
        }
        return sum;
    }
    
    //calculate the number of uncoverd arrays in the results now--3way
    private static int countExistingUncovered3(int cur, int[] index, int[] temp, Integer[] order) {
        int sum = 0;
        if (cur == 1) {
            for (int i = 2; i < K; i++) {
                for (int j = 0; j < l.get(i); j++) {
                    if (!ifContain(index[0], index[1], order[0], temp[order[0]], order[i], j)) {
                        sum++;
                    }
                }
            }
            return sum;
        }

        for (int i = 0; i < cur - 1; i++) {
            for (int j = i + 1; j < cur; j++) {
                if (!ifContain(index[0], index[1], order[i], temp[order[i]], order[j], temp[order[j]]))
                    sum++;
            }
        }
        return sum;
    }
    
    //return the first index
    private static int[] findFirstf() {
        if (T == 2)
            return findFirstf2();
        return findFirstf3();
    }

    //return the first index--2way
    private static int[] findFirstf3() {
        int[] index = new int[4];
        int max = 0;
        int[] temp = new int[4];
        int t = 0;
        for (int i = 0; i < K - 1; i++) {
            for (int j = 0; j < l.get(i); j++) {
                temp[0] = i;
                temp[1] = j;
                for (int k = i + 1; k < K; k++) {
                    for (int m = 0; m < l.get(k); m++) {
                        temp[2] = k;
                        temp[3] = m;
                        t = countUncovered(temp, T);
                        if (t > max) {
                            max = t;
                            index = temp.clone();
                        }
                    }
                }
            }
        }
        if (max == 0)
            return null;

        return index;
    }
    
    //return the first index--3way
    private static int[] findFirstf2() {
        int[] index = new int[2];
        int max = 0;
        int[] temp = new int[2];
        int t = 0;
        for (int i = 0; i < K; i++) {
            for (int j = 0; j < l.get(i); j++) {
                temp[0] = i;
                temp[1] = j;
                t = countUncovered(temp, T);
                if (t > max) {
                    max = t;
                    index[0] = temp[0];
                    index[1] = temp[1];
                }
            }
        }
        if (max == 0)
            return null;

        return index;
    }
    
    //calculate the number of uncoverd arrays with the index[]
    private static int countUncovered(int[] index, int t) {
        if (t == 2)
            return countUncovered2(index);
        return countUncovered3(index);
    }
    
    //calculate the number of uncoverd arrays with the index[]--2way
    private static int countUncovered2(int[] index) {
        int sum = 0;

        for (int i = 0; i < K; i++) {
            if (i == index[0])
                continue;
            for (int j = 0; j < l.get(i); j++) {
                if (!ifContain(index[0], index[1], i, j))
                    sum++;
            }
        }
        return sum;
    }
    
    //calculate the number of uncoverd arrays with the index[]--3way
    private static int countUncovered3(int[] index) {
        int sum = 0;

        for (int i = 0; i < K; i++) {
            if (i == index[0] || i == index[2])
                continue;
            for (int j = 0; j < l.get(i); j++) {
                if (!ifContain(index[0], index[1], index[2], index[3], i, j))
                    sum++;
            }
        }
        /*
         * for (int i = 0; i < K - 1; i++) { if (i == index[0]) continue; for (int j =
         * 0; j < l.get(i); j++) { for (int t = i + 1; t < K; t++) { if (t == index[0])
         * continue; for (int v = 0; v < l.get(t); v++) { if (!ifContain(index[0],
         * index[1], i, j, t, v)) sum++; } } } }
         */
        return sum;
    }
    
    //judge if results have contain the pair--2way 
    private static boolean ifContain(int i1, int v1, int i2, int v2) {
        for (int[] temp : result) {
            if (temp[i1] == v1 && temp[i2] == v2)
                return true;
        }
        return false;
    }
    
    //judge if results have contain the pair--3way 
    private static boolean ifContain(int i1, int v1, int i2, int v2, int i3, int v3) {
        for (int[] temp : result) {
            if (temp[i1] == v1 && temp[i2] == v2 && temp[i3] == v3)
                return true;
        }
        return false;
    }
    
    //factorial
    private static int fac(int a) {
    	if (a <= 1)
            return 1;
        else
            return a * fac(a - 1);
    }
}


class myFrame extends JFrame {
    public myFrame() {
        super();
        setTitle("AETG");
        setSize(300, 300);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        myPanel panel = new myPanel();
        setContentPane(panel);
        setVisible(true);
    }
}

class myPanel extends JPanel {
    myPanel() {
        super();
        final JButton btn2Way = new JButton("2-way");
        btn2Way.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showFileOpenDialog(null, 2);
            }
        });

        final JButton btn3Way = new JButton("3-way");
        btn3Way.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showFileOpenDialog(null, 3);
            }
        });
        add(btn2Way);
        add(btn3Way);
    }

    private static void showFileOpenDialog(Component parent, int t) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setMultiSelectionEnabled(false);

        int result = fileChooser.showOpenDialog(parent);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            // System.out.println(file.getAbsolutePath());
            try {
                Aetg.T = t;
                Workbook workbook = Workbook.getWorkbook(file);

                Sheet sheet = workbook.getSheet(0);
                if (sheet != null) {
                    int rows = sheet.getRows();
                    int cols = sheet.getColumns();
                    Aetg.K = cols;
                    int[] v = new int[Aetg.K];
                    Aetg.values = new String[cols][rows];
                    for (int row = 0; row < rows; row++) {
                        for (int col = 0; col < cols; col++) {
                            Aetg.values[col][row] = sheet.getCell(col, row).getContents();
                        }
                    }

                    for (int i = 0; i < cols; i++) {
                        for (int j = 0; j < rows; j++) {
                            if (Aetg.values[i][j].equals("")) {
                                v[i] = j - 1;
                                break;
                            }
                            v[i] = j;
                        }

                    }
                    Aetg.aetg(Aetg.K, v, Aetg.T);

                    JOptionPane.showMessageDialog(null, "Done", "Done", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

}