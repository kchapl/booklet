import { promises as fs } from 'fs';
import { join } from 'path';

export interface StaticFile {
  fetchContent(path: string): Promise<string>;
}

export class StaticFileLive implements StaticFile {
  async fetchContent(path: string): Promise<string> {
    const filePath = join(__dirname, path);
    const content = await fs.readFile(filePath, 'utf-8');
    return content;
  }
}
