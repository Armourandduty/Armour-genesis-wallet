package com.wallet.crypto.alphawallet.entity;

import org.web3j.crypto.Hash;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wallet.crypto.alphawallet.entity.TransactionDecoder.ReadState.ARGS;

/**
 * Created by James on 2/02/2018.
 *
 * TransactionDecoder currently only decode a transaction input in the
 * string format, which is strictly a string starting with "0x" and
 * with an even number of hex digits followed. (Probably should be
 * bytes but we work with string for now.) It is used only for one
 * thing at the moment: decodeInput(), which returns the decoded
 * input.
 */

// TODO: Should be a factory class that emits an object containing transaction interpretation
public class TransactionDecoder
{
    TransactionInput thisData;

    private int parseIndex;
    private Map<String, FunctionData> functionList;

    private ReadState state = ARGS;
    private int sigCount = 0;

    public TransactionDecoder()
    {
        setupKnownFunctions();
    }

    public TransactionInput decodeInput(String input)
    {
        int parseState = 0;
        parseIndex = 0;
        //1. check function
        thisData = new TransactionInput();

        try {
            while (parseIndex < input.length()) {
                switch (parseState) {
                    case 0: //get function
                        parseIndex += setFunction(input.substring(0, 10), input.length());
                        parseState = 1;
                        break;
                    case 1: //now get params
                        parseIndex += getParams(input);
                        parseState = 2;
                        break;
                    case 2:
                        break;
                }

                if (parseIndex < 0) break; //error
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return thisData;
    }

    public int setFunction(String input, int length) throws Exception
    {
        //first get expected arg list:
        FunctionData data = functionList.get(input);

        if (data != null)
        {
            thisData.functionData = data;
            thisData.paramValues.clear();
            thisData.addresses.clear();
            thisData.sigData.clear();
            thisData.miscData.clear();
        }
        else
        {
            System.out.println("Unhandled Transaction: " + input);
            //unknown
            return length;
        }

        return input.length();
    }

    enum ReadState
    {
        ARGS,
        SIGNATURE
    };

    private int getParams(String input) throws Exception
    {
        state = ARGS;
        if (thisData.functionData.args != null)
        {
            for (String type : thisData.functionData.args)
            {
                String argData = read256bits(input);
                switch (type)
                {
                    case "address":
                        thisData.addresses.add(argData);
                        break;
                    case "bytes32":
                        addArg(argData);
                        break;
                    case "uint16[]":
                        BigInteger count = new BigInteger(argData, 16);
                        for (int i = 0; i < count.intValue(); i++) {
                            thisData.paramValues.add(new BigInteger(read256bits(input), 16));
                        }
                        break;
                    case "uint256":
                        addArg(argData);
                        break;
                    case "uint8": //In our standards, we will put uint8 as the signature marker
                        if (thisData.functionData.hasSig) {
                            state = ReadState.SIGNATURE;
                            sigCount = 0;
                        }
                        addArg(argData);
                        break;
                    case "nodata":
                        //no need to store this data - eg placeholder to indicate presence of a vararg
                        break;
                    default:
                        break;
                }
            }
        }

        return parseIndex;
    }

    private void addArg(String input)
    {
        switch (state)
        {
            case ARGS:
                thisData.miscData.add(input);
                break;
            case SIGNATURE:
                thisData.sigData.add(input);
                if (++sigCount == 3) state = ARGS;
                break;
        }
    }

    private String read256bits(String input)
    {
        if ((parseIndex + 64) <= input.length())
        {
            String value = input.substring(parseIndex, parseIndex+64);
            parseIndex += 64;
            return value;
        }
        else
        {
            return "0";
        }
    }


    private void setupKnownFunctions()
    {
        functionList = new HashMap<>();
        for (int index = 0; index < KNOWN_FUNCTIONS.length; index++)
        {
            String methodSignature = KNOWN_FUNCTIONS[index];
            FunctionData data = getArgs(methodSignature);
            data.hasSig = HAS_SIG[index];
            data.contractType = CONTRACT_TYPE[index];
            functionList.put(buildMethodId(methodSignature), data);
        }
    }

    static final String[] KNOWN_FUNCTIONS = {
            "transferFrom(address,address,uint16[])",
            "transfer(address,uint16[])",
            "trade(uint256,uint16[],uint8,bytes32,bytes32)",
            "transfer(address,uint)",
            "transferFrom(address,address,uint)",
            "approve(address,uint)"
            };

    static final boolean[] HAS_SIG = {
            false,
            false,
            true,
            false,
            false,
            false
    };

    static final int ERC20 = 1;
    static final int ERC875 = 2;

    static final int[] CONTRACT_TYPE = {
            ERC875,
            ERC875,
            ERC875,
            ERC20,
            ERC20,
            ERC20
    };

    private FunctionData getArgs(String methodSig)
    {
        int b1Index = methodSig.indexOf("(");
        int b2Index = methodSig.lastIndexOf(")");

        FunctionData data = new FunctionData();
        data.functionName = methodSig.substring(0, b1Index);
        String args = methodSig.substring(b1Index + 1, b2Index);
        String[] argArray = args.split(",");
        List<String> temp = Arrays.asList(argArray);
        data.args = new ArrayList<>();
        data.args.addAll(temp);
        data.functionFullName = methodSig;

        for (int i = 0; i < temp.size(); i++)//String arg : data.args)
        {
            String arg = temp.get(i);
            if (arg.contains("[]"))
            {
                //rearrange to end, no need to store this arg
                data.args.add(arg);
                String argPlaceholder = "nodata";
                data.args.set(i, argPlaceholder);
            }
        }

        return data;
    }

    private String buildMethodId(String methodSignature) {
        byte[] input = methodSignature.getBytes();
        byte[] hash = Hash.sha3(input);
        return Numeric.toHexString(hash).substring(0, 10);
    }
}

