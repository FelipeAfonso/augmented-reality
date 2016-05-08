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

        public static void start() {
            ipAd = IPAddress.Parse("10.0.0.102");
            server = new TcpListener(ipAd, 1209);
            client = default(TcpClient);
            try {
                server.Start();
                Console.WriteLine("Servidor ouvindo na porta 1209 com sucesso");
            } catch {
                Console.WriteLine("Falha ao iniciar servidor");
            }
        }
        public static bool accept() { try { client = server.AcceptTcpClient(); Console.WriteLine("Cliente conectado com sucesso"); return true; } catch { return false; } }
        public static string receive() {
            byte[] b = new byte[4];
            var encoder = new ASCIIEncoding();
            client.Client.Receive(b);
            client.Close();
            return Encoding.UTF8.GetString(b);
        }
        //public static bool send(String message) { try { var encoder = new ASCIIEncoding(); socket.Send(encoder.GetBytes(message)); return true; } catch { return false; } }
        public static void close() { client.Close(); server.Stop(); }
    }
}
