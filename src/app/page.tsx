import { GameCanvas } from "@/components/GameCanvas";

export default function Home() {
  return (
    <div className="flex flex-1 flex-col items-center justify-center bg-black font-sans">
      <GameCanvas />
    </div>
  );
}
