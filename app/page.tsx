import { redirect } from 'next/navigation';

export default function Home() {
  // Redirect la aplicația principală
  redirect('/index.html');
}
