package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    // group-->> userlist
    private HashMap<Group, List<User>> groupUserMap;
    // group -->> messagelist
    private HashMap<Group, List<Message>> groupMessageMap;
    // message-->> each user
    private HashMap<Message, User> senderMap;
    // group --->> admin
    private HashMap<Group, User> adminMap;
    private HashSet<String> userMobile;
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMobile = new HashSet<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }



    public String createUser(String name, String mobile) throws Exception {

        if(userMobile.contains(mobile))
        {
            throw new Exception("User already exists");
        }
        userMobile.add(mobile);
        return "SUCCESS";
    }

    public Group createGroup(List<User> users) {

        if(users.size()==2)
        {
            Group group = new Group(users.get(1).getName(),2);
            groupUserMap.put(group,users);
            adminMap.put(group,users.get(0));
            return group;
        }

        String s = "Group" + customGroupCount;
        Group group = new Group(s,users.size());
        groupUserMap.put(group,users);
        adminMap.put(group,users.get(0));
        customGroupCount++;
        return group;
    }

    public int createMessage(String content) {

        Message message = new Message(messageId,content);
        messageId++;
        return messageId;
    }

    public int sendMessage(Message message, User sender, Group group) {
        if(!groupUserMap.containsKey(group)) throw new RuntimeException("Group does not exist");
        senderMap.put(message,sender);
        List<User> list = new ArrayList<>(groupUserMap.get(group));
        for(User user : list)
        {
            if(user.getMobile().equals(sender.getMobile()))
            {
                if(groupMessageMap.containsKey(group))
                {
                    groupMessageMap.get(group).add(message);
                    return groupMessageMap.get(group).size();
                }
            }
            else
            {
                List<Message> messages = new ArrayList<>();
                messages.add(message);
                groupMessageMap.put(group,messages);
                return messages.size();
            }
        }
        throw new RuntimeException("You are not allowed to send message");
    }

    public String changeAdmin(User approver, User user, Group group) {

        if(!groupMessageMap.containsKey(group)) throw new RuntimeException("Group does not exist");

        if(!adminMap.get(group).getMobile() .equals(approver.getMobile()))
            throw new RuntimeException("Approver does not have rights");

        List<User> list = new ArrayList<>(groupUserMap.get(group));

        for(User u : list)
        {
            if(u.getMobile().equals(user.getMobile()))
            {
                adminMap.put(group,u);
                return "Success";
            }
        }
        throw new RuntimeException("User is not a participant");
    }

    public int removeUser(User user) {

        for(Group group : groupUserMap.keySet())
        {
            List<User> list = new ArrayList<>(groupUserMap.get(group));

            for(User u : list)
            {
                if(user.getMobile().equals(u.getMobile())) {

                    if(adminMap.get(group).getName().equals(user.getMobile()))
                        throw new RuntimeException("Cannot remove admin");
                    else {
                        List<Message> messages = new ArrayList<>(groupMessageMap.get(group));
                        for(Message message : messages)
                        {
                            if(senderMap.get(message).getMobile().equals(user.getMobile()))
                            {
                                messages.remove(message);
                                senderMap.remove(message);
                            }

                        }
                        groupUserMap.get(group).remove(user);
                        group.setNumberOfParticipants(group.getNumberOfParticipants()-1);
                        return groupUserMap.get(group).size()+groupMessageMap.get(group).size()+senderMap.size();
                    }
        }


            }
        }
        throw  new RuntimeException("User not found");
                           }

    public String findMessage(Date start, Date end, int k) {

        return null;
    }
    }

