/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package form;

import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import javax.swing.JFrame;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import javax.swing.JOptionPane;
import sql.KetNoi;
import java.util.Date;
import java.sql.ResultSet;
/**
 *
 * @author Minh Hieu
 */
public class Order extends javax.swing.JFrame {
    DefaultTableModel model;
    KetNoi db = new KetNoi();
    Connection con;
    ResultSet rs;
    PreparedStatement ps;
    int i = 1;
    /**
     * Creates new form Order
     */
    public Order() {
        initComponents();
        Date date = new Date();
        SimpleDateFormat date1 = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());
        datet.setText(date1.format(date).toString());
        this.setLocationRelativeTo(null);
        cbloai.addActionListener(cbloai);
        loadcb();
        model = (DefaultTableModel) jTable1.getModel();
        model.setColumnIdentifiers(new Object[]{
            "STT","Tên Món","Loại","Đơn giá","Số Lượng","Thành Tiền"
    });
        txttienthoi.setEditable(false);
        txtlast.setEditable(false);
        txtnhanvien.setEditable(false);
    }
    void close(){
        WindowEvent closewd = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(closewd);
    }
    public void loadcb(){
        try{
            con = db.getCon();
            String sql1 = "select tenloai from Loai";
            ps = con.prepareStatement(sql1);
            rs = ps.executeQuery();
            while(rs.next()){
                String s  = rs.getString(1);
                cbloai.addItem(s);
            }
        }catch(Exception e){JOptionPane.showMessageDialog(null, e);}
    }
    public void addHD(){
        try{
            String id = null;
            while(true){
                String se = "select * from employe where HoTen=?";
                PreparedStatement k = con.prepareStatement(se);
                k.setString(1, txtnhanvien.getText());
                ResultSet rsq = k.executeQuery();
                if(rsq.next()){
                    id = rsq.getString(1);
                    
                }
                String sb = "select * from HoaDon where MaHD=?";
                Connection c = db.getCon();
                PreparedStatement l = c.prepareStatement(sb);
                l.setString(1, txtmhd.getText());
                ResultSet rsl = l.executeQuery();
                if(rsl.next()){
                    System.out.println(rsl.getString(1));
                    JOptionPane.showMessageDialog(null, "ID trùng");
                    return;
                }else break;
            }
            
        String kt = "insert into HoaDon values(?,?,?)";
        Connection conkt = db.getCon();
        PreparedStatement pskt = conkt.prepareStatement(kt);
        pskt.setString(1, txtmhd.getText());
        pskt.setString(2, datet.getText());
        pskt.setString(3, id);
        pskt.executeUpdate();
        JOptionPane.showMessageDialog(null, "Lưu thành công");
        }catch(Exception e){JOptionPane.showMessageDialog(null, e);}
    }
    public void addCTHD(){
        int line1 = jTable1.getRowCount();
        try{
            for(int o = 0; o<line1; o++){
                String idmon = null;
                while(true){
                    String ok = "select * from ThucDon where TenMon=?";
                    Connection cok = db.getCon();
                    PreparedStatement psok = cok.prepareStatement(ok);
                    psok.setString(1, jTable1.getValueAt(o, 1).toString());
                    ResultSet rsok = psok.executeQuery();
                    if(rsok.next()){
                        idmon = rsok.getString(1);
                        break;
                    }else break;
                }
                String add = "insert into CTHD values(?,?,?,?)";
                Connection add1 = db.getCon();
                PreparedStatement psa = add1.prepareStatement(add);
                psa.setString(1, txtmhd.getText());
                psa.setString(2, idmon);
                psa.setInt(3, Integer.parseInt(jTable1.getValueAt(o, 4).toString()));
                psa.setInt(4, Integer.parseInt(jTable1.getValueAt(o, 3).toString()));
                psa.executeUpdate();
                
            }
        }catch(Exception e){JOptionPane.showMessageDialog(null, e);}
    }
    public void output(){
        try{
            int line2 = jTable1.getRowCount();
            Writer bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("E:/History//" + txtmhd.getText().trim() + ".txt"), "UTF8"));
            bw.write("\t\t\tHÓA ĐƠN BÁN HÀNG\r\n\r\n");
            bw.write("Mã Hóa Đơn: "+ txtmhd.getText() + "\r\n");
            bw.write("Thời Gian: "+ datet.getText() + "\r\n");
            bw.write("Nhân Viên: "+ txtnhanvien.getText() + "\r\n");
            bw.write("----------------------------------------------------\n");
            bw.write("Tên Sản Phẩm\nĐơn Giá\tSố Lượng\tThành Tiền\r\n");
            bw.write("----------------------------------------------------\n");
            int total = 0;
            for(int c = 0; c<line2; c++){
                String id =  model.getValueAt(c, 0).toString().trim();
                String name = model.getValueAt(c, 1).toString().trim();
                String gia = model.getValueAt(c, 3).toString();
                String sol = model.getValueAt(c, 4).toString();
                String tt = model.getValueAt(c, 5).toString();
                bw.write(name +"\n");
                bw.write( gia+ "\t" +sol+"\t\t"+tt+"\r\n\r\n");
                total += Integer.parseInt(sol);
            }
            bw.write("----------------------------------------------------\n");
            bw.write("Tổng cộng:\t" +Integer.toString(total)+ "\t" +txtlast.getText().toString() +"VNĐ\r\n");
            bw.write("Tiền Khách Đưa:\t\t" + txtmkhach.getText().toString() +"VNĐ\r\n");
            bw.write("Tiền trả lại:\t\t"+txttienthoi.getText().toString()+"VNĐ\r\n");
            bw.write("----------------------------------------------------\n");
            bw.write("-----------------------CẢM ƠN QUÝ KHÁCH----------------");
            bw.close();
            
        }catch(Exception e){System.out.println(e);}
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txttienthoi = new javax.swing.JTextField();
        txtmhd = new javax.swing.JTextField();
        txtmkhach = new javax.swing.JTextField();
        txtlast = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        datet = new javax.swing.JLabel();
        cbloai = new javax.swing.JComboBox<>();
        cbmon = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        jLabel23 = new javax.swing.JLabel();
        txtnhanvien = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();
        sl = new javax.swing.JSpinner();
        jLabel24 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Order");
        setUndecorated(true);

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/delete.png"))); // NOI18N
        jButton2.setText("Xóa");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 90, 31));

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/im/anh-nen-mau-xam_055858999.jpg"))); // NOI18N
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 360, 160));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jPanel2.setForeground(new java.awt.Color(50, 3, 137));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel2.setText("Thanh Toán");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, -1, -1));

        jLabel5.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(204, 51, 0));
        jLabel5.setText("Thành tiền:");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 50, -1, 20));

        jLabel6.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(204, 51, 0));
        jLabel6.setText("Tiền khác đưa:");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, -1, -1));

        jLabel7.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(204, 51, 0));
        jLabel7.setText("Tiền trả lại:");
        jPanel2.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 110, -1, -1));

        jLabel8.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(204, 51, 0));
        jLabel8.setText("Mã hóa đơn:");
        jPanel2.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 140, -1, -1));

        txttienthoi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txttienthoiMouseClicked(evt);
            }
        });
        jPanel2.add(txttienthoi, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 110, 130, -1));
        jPanel2.add(txtmhd, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 140, 130, -1));

        txtmkhach.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtmkhachCaretUpdate(evt);
            }
        });
        txtmkhach.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtmkhachActionPerformed(evt);
            }
        });
        jPanel2.add(txtmkhach, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 80, 130, -1));
        jPanel2.add(txtlast, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 50, 170, -1));

        jButton3.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/printer.png"))); // NOI18N
        jButton3.setText("Lưu và In");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 180, 184, 59));

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/im/anh-nen-mau-xam_055858999.jpg"))); // NOI18N
        jPanel2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 390, 270));

        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        datet.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        datet.setForeground(new java.awt.Color(0, 255, 255));
        datet.setText("jLabel1");
        jPanel3.add(datet, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 20, 120, 20));

        cbloai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbloaiActionPerformed(evt);
            }
        });
        jPanel3.add(cbloai, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 140, -1));

        jPanel3.add(cbmon, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 90, 140, -1));

        jButton1.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 0, 0));
        jButton1.setText("Đăng Xuất");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 11, -1, -1));

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel23.setText("Tên Nhân Viên:");
        jPanel3.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 20, -1, 20));
        jPanel3.add(txtnhanvien, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 20, 194, -1));

        jButton5.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        jButton5.setText("Thêm");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 70, 87, 51));
        jPanel3.add(sl, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 90, 52, -1));

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel24.setText("Số lượng:");
        jPanel3.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 90, -1, 20));

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/im/anh-nen-mau-xam_055858999.jpg"))); // NOI18N
        jPanel3.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(-10, 0, 710, 190));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 365, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Login log = new Login();
        log.setVisible(true);
        close();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void cbloaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbloaiActionPerformed
       cbmon.removeAllItems();
        try{
           Connection conla = db.getCon();
           String sql = "{Call la (?)}";
           CallableStatement ca = conla.prepareCall(sql);
           ca.setString(1, cbloai.getSelectedItem().toString());
           ca.execute();
           ResultSet rss = ca.getResultSet();
           while(rss.next()){
               String sw = rss.getString(2);
               cbmon.addItem(sw);
           }
       }catch(Exception e){JOptionPane.showMessageDialog(null, e);}
    }//GEN-LAST:event_cbloaiActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed

        int soluong = Integer.parseInt(sl.getValue().toString());;
        int line = jTable1.getRowCount();
        while(true){
        if(soluong<=0){
            JOptionPane.showMessageDialog(null, "Số lượng phải lớn hơn 0");
            return;
        }
        if(cbloai.getSelectedIndex() == -1 || cbmon.getSelectedIndex()== -1){
            JOptionPane.showMessageDialog(null, "Combobox rỗng");
            return;
        }else break;
        }

        for (int t = 0; t < line; t++){
            if(jTable1.getValueAt(t, 1).toString().equals(cbmon.getSelectedItem().toString())){
                int cu = Integer.parseInt(jTable1.getValueAt(t, 4).toString());
                int moi = Integer.parseInt(sl.getValue().toString());
                soluong = cu + moi;
                sl.setValue(soluong);
                model.removeRow(t);
                i--;
                break;
            }
        }
        
        try{
           String sql = "select * from ThucDon where TenMon=?";
           PreparedStatement psm = con.prepareStatement(sql);
           psm.setString(1, cbmon.getSelectedItem().toString());
           rs = psm.executeQuery();
           while(rs.next()){
               int gia = Integer.parseInt(rs.getString(4));
                model.addRow(new Object[]{
                i++,cbmon.getSelectedItem().toString(),cbloai.getSelectedItem().toString(),Integer.toString(gia),Integer.parseInt(sl.getValue().toString()),Integer.toString(gia*Integer.parseInt(sl.getValue().toString()))
                });
           }
        int sum =0;
        for(int j=0; j<jTable1.getRowCount();j++){
            sum = sum + Integer.parseInt(jTable1.getValueAt(j, 5).toString());
        }
        txtlast.setText(Integer.toString(sum));
        sl.setValue(1);
       }catch(Exception e){JOptionPane.showMessageDialog(null, e);}
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        txtmkhach.setText("");
        txttienthoi.setText("");
        model.removeRow(jTable1.getSelectedRow());
        int sum =0;
        for(int j=0; j<jTable1.getRowCount();j++){
            sum = sum + Integer.parseInt(jTable1.getValueAt(j, 5).toString());
        }
        txtlast.setText(Integer.toString(sum));// TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void txtmkhachActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtmkhachActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtmkhachActionPerformed

    private void txttienthoiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txttienthoiMouseClicked

    }//GEN-LAST:event_txttienthoiMouseClicked

    private void txtmkhachCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtmkhachCaretUpdate
        try{
        
        int tt = Integer.parseInt(txtlast.getText());
        int tong = 0, khach;
        khach = Integer.parseInt(txtmkhach.getText());
        tong = khach - tt;
        txttienthoi.setText(Integer.toString(tong));
        }catch(Exception e){}// TODO add your handling code here:
    }//GEN-LAST:event_txtmkhachCaretUpdate

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        while(true){
            if(txtlast.getText().equals("") || txtmhd.getText().equals("") || txtmkhach.getText().equals("") || txttienthoi.getText().equals("")){
                JOptionPane.showMessageDialog(null, "Có hàng còn rỗng");
                return;
            }
            if(Integer.parseInt(txttienthoi.getText())<0){
                JOptionPane.showMessageDialog(null, "Khách chưa đưa đủ tiền");
                return;
            }
            if(!txtmhd.getText().matches("HD[0-9]{4}")){
                JOptionPane.showMessageDialog(null, "Mã hóa đơn: HD + 4 số");
                return;
            }else break;
        }
        addHD();
        addCTHD();
        output();
        Runtime run = Runtime.getRuntime();
            try{
                run.exec("notepad E:/History/"+ txtmhd.getText().trim()+".txt");
            }catch(Exception e){JOptionPane.showMessageDialog(null, e);}
    }//GEN-LAST:event_jButton3ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Order.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Order.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Order.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Order.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Order().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cbloai;
    private javax.swing.JComboBox<String> cbmon;
    private javax.swing.JLabel datet;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JSpinner sl;
    private javax.swing.JTextField txtlast;
    private javax.swing.JTextField txtmhd;
    private javax.swing.JTextField txtmkhach;
    public javax.swing.JTextField txtnhanvien;
    private javax.swing.JTextField txttienthoi;
    // End of variables declaration//GEN-END:variables

    
}
