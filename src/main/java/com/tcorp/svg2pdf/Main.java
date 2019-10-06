package com.tcorp.svg2pdf;


public class Main {
    private static Server server;
    public static void main(String[] args) {
        System.out.println("Starting svg2pdf server...");
        if (args.length != 3 && args.length != 1) {
            throw new RuntimeException("Arguments should be [port] ([keystorepath]) ([keystorepassword])");
        } else if (args.length == 3){
            server = new Server(args[1], args[2], Integer.valueOf(args[0]));
    }else
            server= new Server( Integer.valueOf(args[0]));
    }
}
