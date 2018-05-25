package com.alena;

import com.alena.Records.Record;
import com.alena.Records.Review;
import com.alena.Records.Store;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.util.Set;

public class GUI extends JFrame {

    public GUI(Set<Record> JSONSet) {
        JFrame frame = new JFrame("ВК Новости");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int count = 0;

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints allbag = new GridBagConstraints();
        allbag.gridx = 0;

        for (Record rec: JSONSet) {
            JPanel Post = new JPanel();
            Post.setLayout(new GridBagLayout());
            GridBagConstraints bag = new GridBagConstraints();
            int num = 0;
            bag.gridx = 0;

            String[] pics = rec.getPics().toArray(new String[rec.getPics().size()]);
            for (String pic: pics) {
                JLabel label = new JLabel();
                ImageIcon icon = new ImageIcon(pic);
                label.setIcon(icon);
                bag.gridy = num;
                bag.anchor = GridBagConstraints.WEST;
                bag.insets.bottom = 5;
                Post.add(label, bag);
                num++;
            }

            String Text = rec.getText();
            if (Text!="") {
                JTextArea Area = new JTextArea();
                Area.setText(Text);
                Area.setWrapStyleWord(true);
                Area.setLineWrap(true);
                Area.setRows(Area.getLineCount());
                Area.setColumns(55);
                Area.setEditable(false);
                Area.setOpaque(false);
                bag.gridy = num;
                bag.anchor = GridBagConstraints.WEST;
                bag.insets.bottom = 5;
                Post.add(Area, bag);
                num++;
            }

            Review[] reviews = rec.getReviews().toArray(new Review[rec.getReviews().size()]);
            String Reviews = "";
            for (Review review: reviews) {
                Reviews = Reviews + "Оценка: " + String.valueOf(review.getRating()) + "\n" + review.getText() + "\n";
            }
            if (Reviews!="") {
                JTextArea Review = new JTextArea();
                Review.setText(Reviews);
                Review.setWrapStyleWord(true);
                Review.setLineWrap(true);
                Review.setRows(Review.getLineCount());
                Review.setColumns(55);
                Review.setEditable(false);
                Review.setOpaque(false);
                bag.gridy = num;
                bag.anchor = GridBagConstraints.WEST;
                bag.insets.bottom = 5;
                Post.add(Review, bag);
                num++;
            }

            Store[] stores = rec.getStores().toArray(new Store[rec.getStores().size()]);
            String Stores = "";
            for (Store store: stores) {
                Stores = Stores + "Наличие: " + String.valueOf(store.getAvailability()) + "\nМагазин " + store.getName() + "\n";
            }
            if (Stores!="") {
                JTextArea Store = new JTextArea();
                Store.setText(Stores);
                Store.setWrapStyleWord(false);
                Store.setLineWrap(true);
                Store.setRows(Store.getLineCount());
                Store.setColumns(55);
                Store.setEditable(false);
                Store.setOpaque(false);
                bag.gridy = num;
                bag.anchor = GridBagConstraints.WEST;
                bag.insets.bottom = 5;
                Post.add(Store, bag);
                num++;
            }

            Post.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
                    BorderFactory.createEmptyBorder(25,25,25,25)));
            Post.revalidate();
            allbag.gridy = count;
            allbag.anchor = GridBagConstraints.WEST;
            allbag.insets.left = 10;
            allbag.insets.bottom = 50;
            panel.add(Post, allbag);
            count++;
        }
        panel.revalidate();

        JScrollPane Scroll = new JScrollPane(panel);
        Scroll.getVerticalScrollBar().setUnitIncrement(20);
        frame.getContentPane().add(Scroll);

        frame.setPreferredSize(new Dimension(1000, 700));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
