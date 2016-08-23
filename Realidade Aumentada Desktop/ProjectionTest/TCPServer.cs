using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net.Sockets;
using System.Net;

namespace ProjectionTest {
    class TCPServer {

        private static IPAddress ipAd;
        private static TcpListener server;
        private static TcpClient client;
        public static bool InUse = false;

        public static void start() {
            ipAd = IPAddress.Parse(TCPServer.GetLocalIPAddress());
            //ipAd = IPAddress.Parse("10.0.0.170");
            //var ip = new WebClient().DownloadString("http://icanhazip.com");
            //ipAd = IPAddress.Parse(ip.Substring(0,ip.Length-1));
            server = new TcpListener(ipAd, 1209);
            client = default(TcpClient);
            try {
                server.Start();
                Console.WriteLine("Servidor ouvindo na porta 1209 com sucesso");
            } catch (Exception e) {
                Console.WriteLine("Falha ao iniciar servidor: " + e.Message);
            }
        }

        public static bool accept() {
            try {
                client = server.AcceptTcpClient();
                Console.WriteLine("Cliente conectado com sucesso");
                return true;
            } catch { return false; }
        }

        public static string receive() {
            byte[] b = new byte[256];
            var encoder = new ASCIIEncoding();
            client.Client.Receive(b);

            return Encoding.UTF8.GetString(b).Substring(0, Encoding.UTF8.GetString(b).IndexOf("\0"));
        }

        //public static bool send(String message) { try { var encoder = new ASCIIEncoding(); client.Client.Send(encoder.GetBytes(message)); return true; } catch { return false; } }
        public static bool send(String message) {
            try {
                var encoder = new ASCIIEncoding();
                client.Client.Send(encoder.GetBytes(message));
                return true;
            } catch {
                return false;
            }
        }
        public static bool send(byte[] message) {
            try {
                client.Client.Send(message);
                return true;
            } catch {
                return false;
            }
        }
        public static void close() { /*client.Close();*/ server.Stop(); }
        public static void closeClient() { client.Close(); }
        //public static bool isOcuppied() { return inUse; }

        public static string GetLocalIPAddress() {
            var host = Dns.GetHostEntry(Dns.GetHostName());
            foreach (var ip in host.AddressList) {
                if (ip.AddressFamily == AddressFamily.InterNetwork) {
                    return ip.ToString();
                }
            }
            throw new Exception("Local IP Address Not Found!");
        }
    }
}
