package CommonCommand;

import java.io.Serializable;


public enum CommandType implements Serializable{
    LOGIN,SIGNUP,STARTPROJECT,MYPROJECT,ALLPROJECT,GETBRANCH,GETCOMMITS,GERCONTRIBUTORS,SUCCESS,ALREADY_EXISTS , GETPROJECT , GETFILE;  
}
