import { useEditor, EditorContent } from '@tiptap/react';
import StarterKit from '@tiptap/starter-kit';
import { Box, Button, HStack } from '@chakra-ui/react';
import Placeholder from '@tiptap/extension-placeholder';

interface Props {
  value: string;
  onChange: (content: string) => void;
  placeholder?: string;
  width?: string | number;
  height?: string | number;
}

const TextEditor = ({ value, onChange, placeholder, width = '100%', height = '77px' }: Props) => {
  const editor = useEditor({
    extensions: [
      StarterKit.configure({
        bulletList: false,
      }),
      Placeholder.configure({
        placeholder: placeholder || '',
        showOnlyWhenEditable: true,
        showOnlyCurrent: false,
      }),
    ],
    editorProps: {
      attributes: {
        class: 'tiptap-editor',
      },
    },
    content: value,
    onUpdate: ({ editor }) => {
      onChange(editor.getHTML());
    },
  });

  return (
    <Box
      border='1px solid #ccc'
      borderRadius='md'
      p={2}
      w={width}
      h={height}
      overflowY='auto'
      overflowWrap='break-word'>
      {editor && (
        <HStack gap={2} mb={2} flexWrap='wrap'>
          <Button
            size='xs'
            onClick={() => editor.chain().focus().toggleBold().run()}
            fontWeight={editor.isActive('bold') ? 'bold' : 'normal'}
            bg={editor.isActive('bold') ? 'gray.200' : 'transparent'}
            color={editor.isActive('bold') ? 'black' : 'gray.600'}
            _hover={{ bg: 'gray.300' }}>
            Bold
          </Button>
          <Button
            size='xs'
            onClick={() => editor.chain().focus().toggleItalic().run()}
            fontStyle={editor.isActive('italic') ? 'italic' : 'normal'}
            bg={editor.isActive('italic') ? 'gray.200' : 'transparent'}
            color={editor.isActive('italic') ? 'black' : 'gray.600'}
            _hover={{ bg: 'gray.300' }}>
            Italic
          </Button>
        </HStack>
      )}
      <Box h='100%'>
        <EditorContent editor={editor} style={{ height: '100%', overflowY: 'auto' }} />
      </Box>
    </Box>
  );
};

export default TextEditor;
