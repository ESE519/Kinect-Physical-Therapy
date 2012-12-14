package edu.seas.upenn.ese519;

import java.awt.Point;
import java.util.ArrayList;

public class AdaptiveTestSampleView extends SampleView{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean startFlag = false;
	private int updateRate;
	private int repetitions = Constants.DEFAULT_REPETITION;
	private int repetitionCounts;
	
	private double shoulderangle;
	private double elbowangle;
	private int lrotateangle;
	private int rrotateangle;
	
	

	// Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JTextPane jTextPane2;
    private javax.swing.JTextPane jTextPane3;
    private javax.swing.JTextPane jTextPane4;
    private javax.swing.JTextPane jTextPane5;
    private javax.swing.JTextPane jTextPane6;
    // End of variables declaration//GEN-END:variables
	
	public AdaptiveTestSampleView(){
		buildGUI();
		initModel();
	}	
	
	@Override
	public void initModel() {
		jCheckBox1.setSelected(false);
		jCheckBox2.setSelected(false);
		jCheckBox3.setSelected(false);
//		super.shoulderUsed = false;
//		super.elbowUsed = false;
//		super.wristUsed = false;
		super.angleUpdateRate = Math.toRadians(0.5);
		super.lshoulder = new Point(295, 100);
		super.rshoulder = new Point(345, 100);
		super.uarmlen = 50;
		super.larmlen = 50;
		super.handlen = 10;
		repetitionCounts = 0;
		
		
		sflag = true;
		eflag = true;
		lrflag = true;
		rrflag = true;		
		
		System.out.println("lsmin "+ shoulderanglemin);
		System.out.println("lsmax "+ shoulderanglemax);
		System.out.println("lemin "+ elbowanglemin);
		System.out.println("lemax "+ elbowanglemax);
		System.out.println(shoulderUsed);
		System.out.println(elbowUsed);
		System.out.println(wristUsed);
	}
	
	@Override
	public void update() {
		if(shoulderUsed){
			if (sflag){
				shoulderangle += angleUpdateRate;
				if (shoulderangle > shoulderanglemax){
					shoulderangle = shoulderanglemax;
					sflag = false;
				}
			}
			else{
				shoulderangle -= angleUpdateRate;
				if (shoulderangle < shoulderanglemin){
					shoulderangle = shoulderanglemin;
					sflag = true;
//					if(exercise == Constants.SHOULDERTEST)
						repetitionCounts++;
				}		
			}
		}
		
		if(elbowUsed){
			if (eflag){
				elbowangle += angleUpdateRate;
				if (elbowangle > elbowanglemax){
					elbowangle = elbowanglemax;
					eflag = false;
				}
			}
			else{
				elbowangle -= angleUpdateRate;
				if (elbowangle < elbowanglemin){
					elbowangle = elbowanglemin;
					eflag = true;
//					if(exercise == Constants.FOREARMTEST)
						repetitionCounts++;
				}		
			}
		}
		
		if(wristUsed){
			if (lrflag) {
				lrotateangle += angleUpdateRate;
				if (lrotateangle > lrotateanglemax) {
					lrotateangle = lrotateanglemax;
					lrflag = false;
				}
			} else {
				lrotateangle -= angleUpdateRate;
				if (lrotateangle < lrotateanglemin) {
					lrotateangle = lrotateanglemin;
					lrflag = true;
//					if(exercise == Constants.WRISTTEST)
						repetitionCounts++;
				}
			}

			if (rrflag) {
				rrotateangle -= angleUpdateRate;
				if (rrotateangle < rrotateanglemin) {
					rrotateangle = rrotateanglemin;
					rrflag = false;
				}
			} else {
				rrotateangle += angleUpdateRate;
				if (rrotateangle > rrotateanglemax) {
					rrotateangle = rrotateanglemax;
					rrflag = true;
				}
			}
		}
	}

	@Override
	public ArrayList<Double> getAngels() {
		ArrayList<Double> list = new ArrayList<Double>();
		if(elbowUsed){
			list.add(elbowangle); 
		}
		if(shoulderUsed){
			list.add(shoulderangle);			
		}
		if(wristUsed){
			list.add(Math.toRadians((double)lrotateangle));
		}
		return list;
	}

	@Override
	public void setStop() {
		this.startFlag = false;
	}

	@Override
	public boolean isRunning() {
		return this.startFlag;
	}

	@Override
	public int getRepititions() {
		return this.repetitions;
	}

	@Override
	public void setRepititions(int repetitions) {
		this.repetitions = repetitions;
	}

	@Override
	public int getRepititionCounts() {
		return this.repetitionCounts;
	}
	
	@Override
	public void run() {
		startFlag = true;
		while (startFlag && repetitionCounts <= repetitions) {
    		try {
    			this.update();
    			Thread.sleep(updateRate);
    		} catch (InterruptedException e) {
    			System.out.println(e.getMessage());
    		}catch (RuntimeException e) {
				System.out.print(e.getMessage());
			}
    	} 		
	}	
	
	
	public void buildGUI() {
		jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jCheckBox3 = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextPane2 = new javax.swing.JTextPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextPane3 = new javax.swing.JTextPane();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextPane4 = new javax.swing.JTextPane();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextPane5 = new javax.swing.JTextPane();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTextPane6 = new javax.swing.JTextPane();
        jButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();

        jCheckBox1.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        jCheckBox1.setText("Left Elbow");
        jCheckBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBox1ItemStateChanged(evt);
            }
        });

        jCheckBox2.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        jCheckBox2.setText("Left Shoulder");
        jCheckBox2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBox2ItemStateChanged(evt);
            }
        });

        jCheckBox3.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        jCheckBox3.setText("Left Wrist");
        jCheckBox3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBox3ItemStateChanged(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        jLabel1.setText("Min");

        jLabel2.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        jLabel2.setText("Max");

        jTextPane1.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        jTextPane1.setAutoscrolls(false);
        jTextPane1.setEnabled(false);
        jScrollPane1.setViewportView(jTextPane1);

        jTextPane2.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        jTextPane2.setAutoscrolls(false);
        jTextPane2.setEnabled(false);
        jScrollPane2.setViewportView(jTextPane2);

        jTextPane3.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        jTextPane3.setAutoscrolls(false);
        jTextPane3.setEnabled(false);
        jScrollPane3.setViewportView(jTextPane3);

        jTextPane4.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        jTextPane4.setAutoscrolls(false);
        jTextPane4.setEnabled(false);
        jScrollPane4.setViewportView(jTextPane4);

        jTextPane5.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        jTextPane5.setAutoscrolls(false);
        jTextPane5.setEnabled(false);
        jScrollPane5.setViewportView(jTextPane5);

        jTextPane6.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        jTextPane6.setAutoscrolls(false);
        jTextPane6.setEnabled(false);
        jScrollPane6.setViewportView(jTextPane6);

        jButton1.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        jButton1.setText("Set");
        jButton1.setEnabled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 287, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(50, 50, 50)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jCheckBox1)
                                    .addComponent(jCheckBox2)
                                    .addComponent(jCheckBox3))
                                .addGap(74, 74, 74)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(47, 47, 47)
                                        .addComponent(jButton1))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(290, 290, 290)
                                .addComponent(jLabel1)
                                .addGap(52, 52, 52)
                                .addComponent(jLabel2)))
                        .addGap(0, 113, Short.MAX_VALUE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBox1)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBox2)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBox3)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );        
	}
	
	private void jCheckBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBox1ItemStateChanged
        // TODO add your handling code here:
        jTextPane1.setEnabled(!(jTextPane1.isEnabled()));
        jTextPane2.setEnabled(!(jTextPane2.isEnabled()));
        
        if(jCheckBox1.isSelected() || jCheckBox2.isSelected() || jCheckBox3.isSelected()){
            jButton1.setEnabled(true);
        }
        else{
            jButton1.setEnabled(false);
        }
    }//GEN-LAST:event_jCheckBox1ItemStateChanged

    private void jCheckBox2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBox2ItemStateChanged
        // TODO add your handling code here:
        jTextPane3.setEnabled(!(jTextPane3.isEnabled()));
        jTextPane4.setEnabled(!(jTextPane4.isEnabled()));
        
        if(jCheckBox1.isSelected() || jCheckBox2.isSelected() || jCheckBox3.isSelected()){
            jButton1.setEnabled(true);
        }
        else{
            jButton1.setEnabled(false);
        }
    }//GEN-LAST:event_jCheckBox2ItemStateChanged

    private void jCheckBox3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBox3ItemStateChanged
        // TODO add your handling code here:
        jTextPane5.setEnabled(!(jTextPane5.isEnabled()));
        jTextPane6.setEnabled(!(jTextPane6.isEnabled()));
        
        if(jCheckBox1.isSelected() || jCheckBox2.isSelected() || jCheckBox3.isSelected()){
            jButton1.setEnabled(true);
        }
        else{
            jButton1.setEnabled(false);
        }
    }//GEN-LAST:event_jCheckBox3ItemStateChanged

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    	
    	// TODO add your handling code here:
         if (jCheckBox1.isSelected()){
            jCheckBox1.setSelected(false);
            
            try{
    			if(!jTextPane1.getText().equals("") && !jTextPane2.getText().equals("")){
    				super.elbowUsed = true;
    				super.elbowanglemax = Math.toRadians(Integer.parseInt(jTextPane2.getText()));
    				super.elbowanglemin = Math.toRadians(Integer.parseInt(jTextPane1.getText()));
    				System.out.println("Set Elbow Angle Range from " + Math.toDegrees(elbowanglemin) + " to "+ Math.toDegrees(elbowanglemax));
    			}
    		}
    		catch(NumberFormatException ex){
    			System.out.println(ex.getMessage() + " elbow angles");
    		}
        }
        
        if (jCheckBox2.isSelected()){
            jCheckBox2.setSelected(false);
            
            try{
    			if(!jTextPane3.getText().equals("") && !jTextPane4.getText().equals("")){
    				super.shoulderUsed = true;
    				super.shoulderanglemax = Math.toRadians(Integer.parseInt(jTextPane4.getText()));
    				super.shoulderanglemin = Math.toRadians(Integer.parseInt(jTextPane3.getText()));
    				System.out.println("Set Shoulder Angle Range from " + Math.toDegrees(shoulderanglemin) + " to "+ Math.toDegrees(shoulderanglemax));
    			}
    		}
    		catch(NumberFormatException ex){
    			System.out.println(ex.getMessage() + " shoulder angles");
    		}
        }
        
        if (jCheckBox3.isSelected()){
            jCheckBox3.setSelected(false);
            
            try{
    			if(!jTextPane5.getText().equals("") && !jTextPane6.getText().equals("")){
    				super.wristUsed = true;
    				super.lrotateanglemax = Integer.parseInt(jTextPane6.getText());
    				super.lrotateanglemin = Integer.parseInt(jTextPane5.getText());
    				System.out.println("Set Wrist Angle Range from " + lrotateanglemin + " to "+ lrotateanglemax);
    			}
    		}
    		catch(NumberFormatException ex){
    			System.out.println(ex.getMessage() + " wrist angles");
    		}            
        }
        
        if(jTextPane1.getText().equals("") || jTextPane2.getText().equals(""))
        	super.elbowUsed = false;
        
        if(jTextPane3.getText().equals("") || jTextPane4.getText().equals(""))
        	super.shoulderUsed = false;
        
        if(jTextPane5.getText().equals("") || jTextPane6.getText().equals(""))
        	super.wristUsed = false;
        
        System.out.println("Angles will be tested:" + (super.elbowUsed?" elbow ":" ") + (super.shoulderUsed?" shoulder ":" ") + (super.wristUsed?" wrist ":" "));
    }//GEN-LAST:event_jButton1ActionPerformed

}
