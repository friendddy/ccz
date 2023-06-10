package org.ccz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.owlike.genson.Genson;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import java.util.ArrayList;
import java.util.List;


@Contract(name = "energycc1")
@Default
public class UpdateCredit implements ContractInterface {
    private Genson genson = new Genson();

    private enum UpdateCreditErrors {
        Credit_NOT_FOUND,
        Credit_ALREADY_EXISTS
    }

    @Transaction
    public void initLedger(final Context ctx){
        String user1 = JSONObject.toJSONString(new Credit(60,0,true,"u1"));
        String user2 = JSONObject.toJSONString(new Credit(60,0,true,"u2"));
        String user3 = JSONObject.toJSONString(new Credit(60,0,true,"u3"));
        addUser(ctx, user1);
        addUser(ctx, user2);
        addUser(ctx, user3);

    }

    @Transaction
    public String addUser(final Context ctx, final String userMes) {
        ChaincodeStub stub = ctx.getStub();
        Credit user = JSONObject.parseObject(userMes,Credit.class);
        String userJson = JSON.toJSONString(user);
        stub.putStringState(user.getUser(), userJson);
        return stub.getTxId();
    }

    @Transaction
    public String queryAll(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        List<Credit> userList = new ArrayList<>();
        QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");
        for (KeyValue result: results) {
            Credit user = JSON.parseObject(result.getStringValue(), Credit.class);
            //System.out.println(user);
            userList.add(user);
        }
        return JSON.toJSONString(userList);
    }


    @Transaction
    public Credit queryCredit(final Context ctx, final String key){
        ChaincodeStub stub = ctx.getStub();
        String creditState = stub.getStringState(key);

        if (creditState == null || creditState.isEmpty()) {
            String errorMessage = String.format("creditUser %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, UpdateCreditErrors.Credit_NOT_FOUND.toString());
        }

        Credit credit = JSON.parseObject(creditState, Credit.class);

        return credit;
    }

    @Transaction()
    public Credit updateCredit(final Context ctx , final String key , final String tf) {
        ChaincodeStub stub = ctx.getStub();
        String creditState = stub.getStringState(key);
        boolean bool = true;
        if (creditState.isEmpty()) {
            String errorMessage = String.format("creditUser %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, UpdateCreditErrors.Credit_NOT_FOUND.toString());
        }
        Credit credit = JSON.parseObject(creditState, Credit.class);
        double cre = credit.getCredit();
        int num = credit.getNums()+1;
        if(tf == "false"){
            bool = false;
            cre = Math.pow(Math.E , -(num/10)) * cre + 0.5 * (1 - Math.pow(Math.E , -(num/10)));
            num = 0;
        } else if (cre < 100) {
            cre = cre + 0.5 * (1 - Math.pow(Math.E , -(num/10)));

        }else{
            cre = 100;

        }
        Credit credit1 = new Credit(cre , num+1 , bool , credit.getUser());
        String newCreditState = JSON.toJSONString(credit1);
        stub.putStringState(key, newCreditState);
        return credit1;
    }
}
