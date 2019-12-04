/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.jsu.mcis.cs425.project2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.naming.Context;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author JSU
 */
public class Database {
    
     public HashMap getUserInfo(String username){
         
         HashMap<String, String> results = null;
         
         // ...
         try{
             
             Connection conn = getConnection();
             
             String query = "SELECT * FROM user WEHRE username = ?";
             PreparedStatement pstatement = conn.prepareStatement(query);
             pstatement.setString(1, username);
             
             boolean hasresults = pstatement.execute();
             
             if (hasresults) {
                 
                 ResultSet resultset = pstatement.getResultSet();
                 
                 if(resultset.next()) {
                     
                     //create hashmap; add user data from result set
                     //use key name "id" for th eif and "displayname"
                     //display name
                     String id = resultset.getString("id");
                     String displayname = resultset.getString("displayname");
                     
                 }
             }
             
         } catch (SQLException ex) {
             Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
         }
         
         return results;
         
     }
    
     Connection getConnection() {
        
        Connection conn = null;
        
        try {
            
            Context envContext = new InitialContext();
            Context initContext  = (Context)envContext.lookup("java:/comp/env");
            DataSource ds = (DataSource)initContext.lookup("jdbc/db_pool");
            conn = ds.getConnection();
            
        }        
        catch (SQLException | NamingException e) {}
        
        return conn;

    }
     String getSkillsListAsHTML(int userid) {
        String query = "select skills.*,a.userid \n" +
        "from cs425_p2.skills as skills\n" +
        "left join (SELECT * FROM cs425_p2.applicants_to_skills where userid = ?) as a\n" +
        "on skills.id = a.skillsid;";
        StringBuilder results = new StringBuilder();
        String skills;
        
        try {
            Connection conn = getConnection();
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, Integer.toString(userid));
            boolean hasResults = statement.execute();
            ResultSet resultset = statement.getResultSet();
            
            while (resultset.next()) {
                String description = resultset.getString("description");
                int id = resultset.getInt("id");
                int user = resultset.getInt("userid");
                results.append("<input type=\"checkbox\" name=\"skills\" value=\"");
                results.append(id);
                results.append("\" id=\"skills_").append(id).append("\" ");
                
                if (user != 0) {
                    results.append("checked");
                }
                
                results.append(">");
                results.append("<label for=\"skills_").append(id).append("\">").append(description).append("</label><br /><br />");
            }
            resultset.close();
            statement.close();
        }
        catch (Exception SQLException) {}
        skills = results.toString();
        
        return skills;
    }
    
    public void setSkillsList(int userid, String[] skills) {
        String query1 = "DELETE FROM cs425_p2.applicants_to_skills WHERE userid = ?;";
        String query2 = "INSERT INTO cs425_p2.applicants_to_skills (userid, skillsid)\n" +
                        "VALUES (?, ?);";
        
        try {
            Connection conn = getConnection();
            PreparedStatement statement = conn.prepareStatement(query1);
            statement.setString(1, Integer.toString(userid));
            int rowsChanged = statement.executeUpdate();
                statement = conn.prepareStatement(query2);
                for (int i = 0; i < skills.length; i++) {
                    statement = conn.prepareStatement(query2);
                    statement.setString(1, Integer.toString(userid));
                    statement.setString(2, skills[i]);
                    statement.execute();             
                }
        }
        catch (Exception SQLException) {}
    }

    String getJobsListAsHTML(int userid, String[] skills) {
        String query1 = "SELECT jobs.id, jobs.name, a.userid FROM\n" +
        "jobs LEFT JOIN (SELECT * FROM applicants_to_jobs WHERE userid= ?) AS a\n" +
        "ON jobs.id = a.jobsid\n" +
        "WHERE jobs.id IN\n" +
        "(SELECT jobsid AS id FROM\n" +
        "(applicants_to_skills JOIN skills_to_jobs\n" +
        "ON applicants_to_skills.skillsid = skills_to_jobs.skillsid)\n" +
        "WHERE applicants_to_skills.userid = ?)\n" +
        "ORDER BY jobs.name;";
        StringBuilder results = new StringBuilder();
        String[] jobsids;
        
        try {
            Connection conn = getConnection();
            PreparedStatement statement = conn.prepareStatement(query1);
                statement.setString(1, Integer.toString(userid));
                statement.setString(2, Integer.toString(userid));
                statement.execute();
                ResultSet resultset = statement.getResultSet();
                while (resultset.next()) {
                    int jobid = resultset.getInt("id");
                    String jobname = resultset.getString("name");
                    int isChecked = resultset.getInt("userid");
                    results.append("<input type=\"checkbox\" name=\"jobs\" value=\"");
                    results.append(jobid);
                    results.append("\" id=\"job_").append(jobid).append("\" ");
                
                    if (isChecked != 0) {
                        results.append("checked");
                    }
                
                    results.append(">");
                    results.append("<label for=\"job_").append(jobid).append("\">").append(jobname).append("</label><br /><br />");
                }
        }
        catch(Exception SQLException){}
        return results.toString();
    }

    void setJobsList(int userid, String[] jobs) {
        String query1 = "DELETE FROM cs425_p2.applicants_to_jobs WHERE userid = ?;";
        String query2 = "INSERT INTO cs425_p2.applicants_to_jobs (userid, jobsid)\n" +
                        "VALUES (?, ?);";
        
        try {
            Connection conn = getConnection();
            PreparedStatement statement = conn.prepareStatement(query1);
            statement.setString(1, Integer.toString(userid));
            int rowsChanged = statement.executeUpdate();
                statement = conn.prepareStatement(query2);
                for (int i = 0; i < jobs.length; i++) {
                    statement = conn.prepareStatement(query2);
                    statement.setString(1, Integer.toString(userid));
                    statement.setString(2, jobs[i]);
                    statement.execute();             
                }
        }
        catch (Exception SQLException) {}
    }

    String getJobsAsHTML(int id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
    

