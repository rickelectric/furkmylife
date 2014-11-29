package rickelectric.furkmanager.utils;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.*;

import javax.swing.*;

public class TimeInterface{
	
	private static Calendar c=null;
	
	public static void main(String[] argv) throws IOException{
		System.out.println(timeToString());
		String d1=selectDate(null),d2=selectDate(null);
		int cmp=compareMyDate(d1,d2);
		if(cmp==0) System.out.println("Dates are the same");
		if(cmp>0) System.out.println("d1>d2");
		if(cmp<0) System.out.println("d1<d2");
	}
	
	public static String fileDate(File f){
		Path path=f.toPath();
		BasicFileAttributes attributes;
		try{
			attributes=Files.readAttributes(path,BasicFileAttributes.class);
			FileTime creationTime = attributes.creationTime();
			String date=creationTime.toString().substring(0,10);
			String[] ds=date.split("-");
			return ds[2]+"-"+monthToString(Integer.parseInt(ds[1]))+"-"+ds[0];
		}catch (IOException e){e.printStackTrace();return null;}
	}
	
	public static String timeToString(){
		if(c==null) c=Calendar.getInstance();
		return c.get(Calendar.DAY_OF_MONTH)+"-"+monthToString(c.get(Calendar.MONTH)+1)+"-"+c.get(Calendar.YEAR);
	}
	
	public static String sqlToFdate(String sqlDate){
		if(sqlDate==null) return null;
		String[] ssp=sqlDate.split("-");
		try{
			return ssp[2]+"-"+monthToString(Integer.parseInt(ssp[1]))+"-"+ssp[0];
		}catch(Exception e){return null;}
	}
	
	public static String fdateToSql(String fdate){
		if(fdate==null) return null;
		String[] ffp=fdate.split("-");
		try{
			return ffp[2]+"-"+stringToMonth(ffp[1])+"-"+ffp[0];
		}catch(Exception e){return null;}
	}
	
	/**
	 * 
	 * @param m Integer representation of the month
	 * @return 3 character string representation of the month
	 */
	public static String monthToString(int m){
		switch(m){
			case 1 :return "Jan";
			case 2 :return "Feb";
			case 3 :return "Mar";
			case 4 :return "Apr";
			case 5 :return "May";
			case 6 :return "Jun";
			case 7 :return "Jul";
			case 8 :return "Aug";
			case 9 :return "Sep";
			case 10:return "Oct";
			case 11:return "Nov";
			case 12:return "Dec";
			default:return null;
		}
	}
	
	/**
	 * Converts a 3 character month string to the month number
	 * @param mon 3 charater string representation of the month
	 * @return Interger representation of the month (as a string)
	 */
	private static String stringToMonth(String mon){
		if(mon.equals("Jan")) return "01";
		if(mon.equals("Feb")) return "02";
		if(mon.equals("Mar")) return "03";
		if(mon.equals("Apr")) return "04";
		if(mon.equals("May")) return "05";
		if(mon.equals("Jun")) return "06";
		if(mon.equals("Jul")) return "07";
		if(mon.equals("Aug")) return "08";
		if(mon.equals("Sep")) return "09";
		if(mon.equals("Oct")) return "10";
		if(mon.equals("Nov")) return "11";
		if(mon.equals("Dec")) return "12";
		return "00";
	}
	
	/**
	 * Compare d1 with d2. 
	 * @param d1 A Date
	 * @param d2 Date To Compare To d1
	 * @return +ve if d1 after d2, -ve if d1 before d2, 0 if same day.
	 */
	public static int compareMyDate(String d1,String d2){
		String[] ds1=d1.split("-"),ds2=d2.split("-");
		int cc=ds1[2].compareTo(ds2[2]);
		if(cc!=0) return cc;
		cc=Integer.parseInt(stringToMonth(ds1[1]))-Integer.parseInt(stringToMonth(ds2[1]));
		if(cc!=0) return cc;
		cc=ds1[0].compareTo(ds2[0]);
		return cc;
	}
	
	public static String selectDate(String def){
		String[] currDate=null;
		if(def==null||def.equals("")){def=null;currDate=fdateToSql(timeToString()).split("-");}
		else if(def.length()==10) currDate=def.split("-");
		else currDate=fdateToSql(def).split("-");
		
		JPanel date=new JPanel(new GridLayout(0,3));
		
		date.add(new JLabel("Day"));
		date.add(new JLabel("Month"));
		date.add(new JLabel("Year"));
		final JTextField year=new JTextField(5);
		final Choice month=new Choice();
		final Choice day=new Choice();
		
		date.add(day);
		date.add(month);
		date.add(year);
		
		for(int i=1;i<=31;i++) day.add(""+i);
		
		month.add("January");month.add("February");
		month.add("March");month.add("April");
		month.add("May");month.add("June");
		month.add("July");month.add("August");
		month.add("September");month.add("October");
		month.add("November");month.add("December");
		month.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent event){
				int sItem=day.getSelectedIndex();
				day.removeAll();
				String selMonth=month.getSelectedItem();
				
				for(int i=1;i<=28;i++) day.add(""+i);
				
				int yr=1;
				if(!year.getText().equals("")) yr=Integer.parseInt(year.getText());
				if(selMonth.equals("February")){
					boolean four=(yr%4==0),onehundred=(yr%100==0),fourhundred=(yr%400==0);
					if((four&&!onehundred)||(four&&onehundred&&fourhundred))
						day.add("29");
				}
				else if(selMonth.equals("September")||selMonth.equals("April")||
					selMonth.equals("June")||selMonth.equals("November")){
					day.add("29");
					day.add("30");
				}
				else{
					day.add("29");
					day.add("30");
					day.add("31");
				}
				try{day.select(sItem);}catch(Exception e){}
			}
		});
		
		year.addKeyListener(new KeyAdapter(){
				@Override
				public void keyReleased(KeyEvent event){
					day.removeAll();
					String selMonth=month.getSelectedItem();
					
					for(int i=1;i<=28;i++) day.add(""+i);
					
					int yr=1;
					if(!year.getText().equals("")) yr=Integer.parseInt(year.getText());
					if(selMonth.equals("February")){
						boolean four=(yr%4==0),onehundred=(yr%100==0),fourhundred=(yr%400==0);
						if((four&&!onehundred)||(four&&onehundred&&fourhundred))
							day.add("29");
					}
					else if(selMonth.equals("September")||selMonth.equals("April")||
						selMonth.equals("June")||selMonth.equals("November")){
						day.add("29");
						day.add("30");
					}
					else{
						day.add("29");
						day.add("30");
						day.add("31");
					}
				}
		});
		month.select(Integer.parseInt(currDate[1])-1);
		year.setText(currDate[0]);
		month.getItemListeners()[0].itemStateChanged(null);
		day.select(currDate[2]);
		int rOpt=JOptionPane.showConfirmDialog(null,date,"Select Date",JOptionPane.OK_CANCEL_OPTION);
		if(rOpt==JOptionPane.CANCEL_OPTION||rOpt==JOptionPane.CLOSED_OPTION) return null;
		if(year.getText().equals("")||Integer.parseInt(year.getText())<=1800||Integer.parseInt(year.getText())>2990){throw new RuntimeException("Invalid Year");}
		String upDate=day.getSelectedItem()+"-"+monthToString(month.getSelectedIndex()+1)+"-"+year.getText();
		return upDate;
	}
	
}
