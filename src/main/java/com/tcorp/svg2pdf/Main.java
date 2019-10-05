package com.tcorp.svg2pdf;


public class Main {
    private static Server server;
    public static void main(String[] args) {
        System.out.println("Starting svg2pdf server...");
        if(args.length != 3){
            throw new RuntimeException("Arguments should be [port] [keystorepath] [keystorepassword]");
        }
        server= new Server(args[1], args[2], Integer.valueOf(args[0]));
    }
}
